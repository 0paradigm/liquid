/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.zeroparadigm.liquid.git.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import com.alibaba.fastjson.JSON;
import io.zeroparadigm.liquid.common.api.core.GitMetaService;
import io.zeroparadigm.liquid.common.bo.UserBO;
import io.zeroparadigm.liquid.common.dto.Result;
import io.zeroparadigm.liquid.git.pojo.LatestCommitInfo;
import io.zeroparadigm.liquid.git.service.GitWebService;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.tika.Tika;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.BranchConfig;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Min;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


@Slf4j
@Service
@RestController
public class GitWebServiceImpl implements GitWebService {

    @Value("${storage.git}")
    private String gitStorage;

    @Value("${storage.git-tmp}")
    private String gitTmpStorage;

    @Value("${storage.git-cache}")
    private String gitCacheStorage;

    @Value("${git.cache-num}")
    @Min(value = 1, message = "in memory cache is not supported yet")
    private int cacheObjNum;

    @DubboReference(parameters = {"unicast", "false"})
    GitMetaService gitMetaService;

    private static final String TMPDIR_PATTERN = "%s-%s";

    @PostConstruct
    void deleteWorkDirOnJvmExit() {
        new File(gitTmpStorage).deleteOnExit();
        new File(gitCacheStorage).deleteOnExit();
    }

    @Async
    @SneakyThrows
    void deleteDirAsync(File dir) {
        FileUtils.deleteDirectory(dir);
    }

    @Override
    public String uploadFile(String owner, String repo, String fromBranch,
                             MultipartFile file, @Nullable String relPath,
                             String taskId) throws IOException, GitAPIException {

        if (Objects.isNull(relPath)) {
            relPath = "";
        }

        File tmpRepo =
            Path.of(gitTmpStorage, owner, String.format(TMPDIR_PATTERN, repo, taskId)).toFile();
        if (Files.notExists(tmpRepo.toPath())) {
            File originalRepo = Path.of(gitStorage, owner, repo).toFile();

            boolean originAlreadyInit;
            try (Git origin = Git.open(originalRepo)) {
                originAlreadyInit = !origin.branchList().call().isEmpty();
            } catch (Exception e) {
                log.error("error fetching branch list of remote {}/{}", owner, repo, e);
                originAlreadyInit = false;
            }

            if (originAlreadyInit) {
                Git.cloneRepository()
                    .setURI(originalRepo.toURI().toString())
                    .setDirectory(tmpRepo)
                    .setBranchesToClone(Collections.singleton("refs/heads/" + fromBranch))
                    .setBranch(fromBranch)
                    .call()
                    .close();
            } else {
                Git.cloneRepository()
                    .setURI(originalRepo.toURI().toString())
                    .setDirectory(tmpRepo)
                    .setBranch(fromBranch)
                    .call()
                    .close();
            }
        }

        Path absPath = Path.of(tmpRepo.toString(), relPath);
        Files.createDirectories(absPath);
        Path dest = Path.of(tmpRepo.toString(), relPath, file.getOriginalFilename());
        FileUtils.createParentDirectories(dest.toFile());
        log.debug("transforming uploaded file '{}' to fs://{}", file.getOriginalFilename(), dest);
        file.transferTo(dest);
        return Path.of(relPath, file.getOriginalFilename()).toString();
    }

    @Override
    public void commit(String owner, String repo, @Nullable String toBranch, String taskId,
                       @Nullable List<String> addFiles, String message, @Nullable UserBO committer)
        throws IOException, GitAPIException {

        String committerName;
        String committerEmail;
        if (committer == null) {
            committerName = "liquid-user";
            committerEmail = "";
        } else {
            committerName = committer.getLogin();
            committerEmail = committer.getEmail();
        }

        File tmpRepo =
            Path.of(gitTmpStorage, owner, String.format(TMPDIR_PATTERN, repo, taskId)).toFile();

        try (Git git = Git.open(tmpRepo)) {
            if (StringUtils.hasText(toBranch)) {
                try {
                    log.info("checking out branch {}", toBranch);
                    git.checkout()
                        .setName(toBranch)
                        .call();
                } catch (RefNotFoundException e) {
                    try {
                        git.checkout()
                            .setCreateBranch(true)
                            .setName(toBranch)
                            .call();
                        log.info("new branch '{}' created", toBranch);
                    } catch (RefNotFoundException e1) {
                        log.warn("committing to new repo, init branch may be dropped");
                    }
                }
            }

            if (Objects.isNull(addFiles) || addFiles.isEmpty()) {
                git.add()
                    .addFilepattern(".")
                    .call();
            } else {
                for (String file : addFiles) {
                    git.add()
                        .addFilepattern(file)
                        .call();
                }
            }
            git.commit()
                .setAllowEmpty(false)
                .setMessage(message)
                .setCommitter(committerName, committerEmail)
                .call();
            gitMetaService.recordContributor(owner, repo, committerName);
            String refSpec = git.getRepository().getBranch() + ":" + toBranch;
            log.info("pushing to remote, spec={}", refSpec);
            git.push()
                .setRemote("origin")
                .setRefSpecs(new RefSpec(refSpec))
                .call();

            updateCaches(owner, repo);
        }
    }

    public void rmCommit(String owner, String repo, @Nullable String toBranch, String taskId,
                         String rm, String message,
                         @Nullable UserBO committer)
        throws IOException, GitAPIException {

        String committerName;
        String committerEmail;
        if (committer == null) {
            committerName = "liquid-user";
            committerEmail = "";
        } else {
            committerName = committer.getLogin();
            committerEmail = committer.getEmail();
        }

        File tmpRepo =
            Path.of(gitTmpStorage, owner, String.format(TMPDIR_PATTERN, repo, taskId)).toFile();

        try (Git git = Git.open(tmpRepo)) {
            if (StringUtils.hasText(toBranch)) {

                try {
                    git.checkout()
                        .setName(toBranch)
                        .call();
                } catch (RefNotFoundException e) {
                    try {
                        git.checkout()
                            .setCreateBranch(true)
                            .setName(toBranch)
                            .call();
                        log.info("new branch '{}' created", toBranch);
                    } catch (RefNotFoundException e1) {
                        log.warn("committing to new repo, init branch may be dropped");
                    }
                }
            }

            rm = rm.strip().replaceAll(" ", "\\ ");
            log.info("rm file {}", rm);
            git.rm()
                .addFilepattern(rm)
                .call();
            git.commit()
                .setAllowEmpty(false)
                .setMessage(message)
                .setCommitter(committerName, committerEmail)
                .call();
            gitMetaService.recordContributor(owner, repo, committerName);

            String refSpec = git.getRepository().getBranch() + ":" + toBranch;
            log.info("pushing to remote, spec={}", refSpec);
            git.push()
                .setRemote("origin")
                .setRefSpecs(new RefSpec(refSpec))
                .call();

            updateCaches(owner, repo);
            deleteDirAsync(tmpRepo);
        }
    }

    int i = 0;

    private synchronized File selectCache(String owner, String repo) {
        for (; ; i++, i %= cacheObjNum) {
            File cacheRepo =
                Path.of(gitCacheStorage, owner, String.format("%s-%d", repo, i)).toFile();
            if (!Files.exists(cacheRepo.toPath())) {
                createCache(owner, repo);
                continue;
            }
            if (Files.exists(Path.of(cacheRepo.getPath(), ".git", "index.lock"))) {
                log.warn("cache {} is busy", i);
                continue;
            }
            i++;
            i %= cacheObjNum;
            log.info("using cache {} for {}/{}", i, owner, repo);
            return cacheRepo;
        }
    }

    @SneakyThrows
    private void createCache(String owner, String repo) {
        log.info("creating {} caches for {}/{}", cacheObjNum, owner, repo);
        File dest0 = Path.of(gitCacheStorage, owner, repo + "-0").toFile();
        FileUtils.deleteQuietly(dest0);
        try {
            Git.cloneRepository()
                .setURI(Path.of(gitStorage, owner, repo).toUri().toString())
                .setDirectory(dest0)
                .call()
                .close();
        } catch (TransportException e) {
            Optional<String> branch = Git.lsRemoteRepository()
                .setRemote(Path.of(gitStorage, owner, repo).toUri().toString())
                .call()
                .stream()
                .map(Ref::getName)
                .findFirst();

            if (branch.isEmpty()) {
                log.error("no branch to clone");
                return;
            }
            Git.cloneRepository()
                .setURI(Path.of(gitStorage, owner, repo).toUri().toString())
                .setDirectory(dest0)
                .setCloneAllBranches(true)
                .setBranch(branch.get())
                .call()
                .close();
        }

        for (int i = 1; i < cacheObjNum; i++) {
            File cacheRepo =
                Path.of(gitCacheStorage, owner, String.format("%s-%d", repo, i)).toFile();
            FileUtils.deleteQuietly(cacheRepo);
            FileUtils.copyDirectory(dest0, cacheRepo);
        }
    }

    public void updateCaches(String owner, String repo) {
        for (int i = 0; i < cacheObjNum; i++) {
            File cacheRepo =
                Path.of(gitCacheStorage, owner, String.format("%s-%d", repo, i)).toFile();
            try (Git git = Git.open(cacheRepo)) {
                git.fetch()
                    .setRemoveDeletedRefs(true)
                    .setForceUpdate(true)
                    .call();
                git.pull()
                    .setRebase(BranchConfig.BranchRebaseMode.REBASE)
                    .call();
            } catch (IOException e) {
                createCache(owner, repo);
            } catch (GitAPIException e) {
                FileUtils.deleteQuietly(cacheRepo);
                createCache(owner, repo);
                log.error("error updating cache", e);
            }
        }
    }

    private void cacheCheckout(Git git, String branch) throws GitAPIException {
        try {
            git.checkout()
                .setName(branch)
                .call();
        } catch (RefNotFoundException e) {
            git.checkout()
                .setForced(true)
                .setCreateBranch(true)
                .setName(branch)
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                .setStartPoint("origin/" + branch)
                .call();
        }
    }

    @Override
    public List<LatestCommitInfo> listFiles(String owner,
                                            String repo,
                                            String branchOrCommit,
                                            @Nullable String relPath)
        throws IOException, GitAPIException {
        relPath =
            URLDecoder.decode(Objects.requireNonNullElse(relPath, ""), StandardCharsets.UTF_8);
        File repoRoot = selectCache(owner, repo);
        File repoFs = Path.of(repoRoot.getPath(), relPath).toFile();

        if (!repoFs.exists()) {
            throw new FileNotFoundException(repoFs.getCanonicalPath());
        }
        try (Git git = Git.open(repoRoot)) {
            cacheCheckout(git, branchOrCommit);

            return Arrays.stream(Objects.requireNonNullElse(repoFs.listFiles(), new File[0]))
                .parallel()
                .map(f -> repoRoot.toPath().relativize(f.toPath()))
                .map(Path::toFile)
                .filter(f -> !".git".equals(f.getName()))
                .map(f -> new LatestCommitInfo(git, f))
                .sorted(Comparator.comparing(LatestCommitInfo::getName))
                .toList();
        } catch (RefNotFoundException e) {
            log.error("branch not found", e);
            return List.of();
        }
    }

    /*
        Return JSON String, format of { fileName: {"oldValue": "", "newValue": "123"} }
     */
    @Override
    public String listFilesChangesOfCommit(String owner,
                                                 String repo,
                                                 String sha1
    )
        throws IOException, GitAPIException {
        File repoRoot = selectCache(owner, repo);
        try (Git git = Git.open(repoRoot)) {
            ObjectId commitId = ObjectId.fromString(sha1);
            RevWalk revWalk = new RevWalk(git.getRepository());

            RevCommit headCommit = revWalk.parseCommit(commitId);
            RevCommit diffWith = headCommit.getParent(0);
            DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
            diffFormatter.setRepository(git.getRepository());
            List<DiffEntry> diffEntries = diffFormatter.scan(diffWith, headCommit);
            ObjectReader objectReader = git.getRepository().newObjectReader();

            Map<String, String> changes = new HashMap<>();
            for (DiffEntry entry : diffEntries) {
                Map<String, String> tmp = new HashMap<>();
                byte[] oldContent = objectReader.open(entry.getOldId().toObjectId()).getBytes();
                byte[] newContent = objectReader.open(entry.getNewId().toObjectId()).getBytes();
                tmp.put("oldValue", new String(oldContent));
                tmp.put("newValue", new String(newContent));
                changes.put(entry.getNewPath(), JSON.toJSONString(tmp));
            }
            return JSON.toJSONString(changes);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return "";
        }

    }

    @Override
    public List<String> findBranchCommit(String owner,
                                         String repo,
                                         String branchOrCommit
    )
        throws IOException, GitAPIException {
        File repoRoot = selectCache(owner, repo);
        try (Git git = Git.open(repoRoot)) {
            List<String> commits = new ArrayList<>();
            Iterable<RevCommit> logs = git.log()
                .not(git.getRepository().resolve("master"))
                // Is revstr right
                .add(git.getRepository().resolve("origin/" + branchOrCommit))
                .call();
            for (RevCommit rev : logs) {
                commits.add(rev.toString());
            }
            return commits;
        } catch (RefNotFoundException e) {
            log.error("branch not found", e);
            return List.of();
        }
    }

    public List<BriefCommitDTO> listPRCommit(String headOwner,
                                             String headRepo,
                                             String baseOwner,
                                             String baseRepo)
        throws IOException, GitAPIException {
        File repoRoot = selectCache(headOwner, headRepo);

        try (Git git = Git.open(repoRoot)) {
            git.fetch()
                .setRemote("origin")
                .call();
            List<BriefCommitDTO> commits = new ArrayList<>();
            git.log()
                .addRange(git.getRepository().resolve("origin/master"),
                    git.getRepository().resolve("HEAD"))
                .call()
                .forEach(commit -> {
                    var brief = BriefCommitDTO.builder()
                        .id(commit.getId().getName())
                        .user(commit.getAuthorIdent().getName())
                        .ts(commit.getCommitTime())
                        .label(commit.getShortMessage())
                        .build();
                    commits.add(brief);
                });
            ;
            return commits;
        } catch (RefNotFoundException e) {
            log.error("branch not found", e);
            return List.of();
        }
    }

    /*
        Return JSON String, format of { fileName: {"oldValue": "", "newValue": "123"} }
     */
    public String listFilesChangesOfRepo(String headOwner,
                                               String headRepo,
                                               String baseOwner,
                                               String baseRepo)
        throws IOException, GitAPIException {
        File headRepoRoot = selectCache(headOwner, headRepo);
        File baseRepoRoot = selectCache(baseOwner, baseRepo);
        try (Git headGit = Git.open(headRepoRoot); Git baseGit = Git.open(baseRepoRoot)) {
            RevCommit oldCommit =
                baseGit.log().add(baseGit.getRepository().resolve("HEAD")).setMaxCount(1).call()
                    .iterator().next();
            RevTree oldTree = oldCommit.getTree();

            RevCommit newCommit =
                headGit.log().add(headGit.getRepository().resolve("HEAD")).setMaxCount(1).call()
                    .iterator().next();
            RevTree newTree = newCommit.getTree();

            DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
            df.setRepository(baseGit.getRepository());
            df.setDiffComparator(RawTextComparator.DEFAULT);
            df.setDetectRenames(true);
            List<DiffEntry> diffs = df.scan(oldTree, newTree);
            ObjectReader objectReader = headGit.getRepository().newObjectReader();

            Map<String, String> changes = new HashMap<>();
            for (DiffEntry diff : diffs) {
                Map<String, String> tmp = new HashMap<>();
                byte[] oldContent = objectReader.open(diff.getOldId().toObjectId()).getBytes();
                byte[] newContent = objectReader.open(diff.getNewId().toObjectId()).getBytes();
                tmp.put("oldValue", new String(oldContent));
                tmp.put("newValue", new String(newContent));
                changes.put(diff.getNewPath(), JSON.toJSONString(tmp));
            }
            return JSON.toJSONString(changes);
        } catch (RefNotFoundException e) {
            log.error("branch not found", e);
            return "";
        }
    }


    Tika tika = new Tika();

    @Override
    public String getFile(String owner, String repo, String branchOrCommit,
                          String filePath) throws IOException {
        File repoRoot = selectCache(owner, repo);
        filePath = URLDecoder.decode(filePath, StandardCharsets.UTF_8);


        try (Git git = Git.open(repoRoot)) {
            cacheCheckout(git, branchOrCommit);

            File file = new File(repoRoot, filePath);
            String extName = FileUtil.extName(file);
            String fileType = tika.detect(file);
            Map<String, String> map = new HashMap<>(2);
            map.put("extName", extName);
            System.out.println(filePath + "  " + extName + "  " + fileType);
            if (fileType.startsWith("text/")) {
                FileReader fileReader = new FileReader(file);
                map.put("content", fileReader.readString());
            } else {
                map.put("content", "<Binary file not shown, please download to local>");
            }
            return JSON.toJSONString(map);
        } catch (Exception e) {
            throw new IOException();
        }
    }

    @Override
    @SneakyThrows
    public Result webDelete(String owner, String repo, String initBranch, String deleteFile,
                            UserBO committer, String message) {
        String taskId = String.valueOf(System.currentTimeMillis());
        File tmpRepo =
            Path.of(gitTmpStorage, owner, String.format(TMPDIR_PATTERN, repo, taskId)).toFile();
        if (Files.notExists(tmpRepo.toPath())) {
            File originalRepo = Path.of(gitStorage, owner, repo).toFile();

            boolean originAlreadyInit;
            try (Git origin = Git.open(originalRepo)) {
                originAlreadyInit = !origin.branchList().call().isEmpty();
            } catch (Exception e) {
                log.error("error fetching branch list of remote {}/{}", owner, repo, e);
                originAlreadyInit = false;
            }

            if (originAlreadyInit) {
                Git.cloneRepository()
                    .setURI(originalRepo.toURI().toString())
                    .setDirectory(tmpRepo)
                    .setBranchesToClone(Collections.singleton("refs/heads/" + initBranch))
                    .setBranch(initBranch)
                    .call()
                    .close();
            } else {
                Git.cloneRepository()
                    .setURI(originalRepo.toURI().toString())
                    .setDirectory(tmpRepo)
                    .setBranch(initBranch)
                    .call()
                    .close();
            }
        }

        rmCommit(owner, repo, initBranch, taskId, deleteFile, message, committer);
        return Result.success();
    }

    @Override
    @SneakyThrows
    public void webRevert(String owner, String repo, String branch, String toSha,
                          UserBO committer) {
        String committerName;
        String committerEmail;
        if (committer == null) {
            committerName = "liquid-user";
            committerEmail = "";
        } else {
            committerName = committer.getLogin();
            committerEmail = committer.getEmail();
        }

        String taskId = String.valueOf(System.currentTimeMillis());
        File tmpRepo =
            Path.of(gitTmpStorage, owner, String.format(TMPDIR_PATTERN, repo, taskId)).toFile();
        if (Files.notExists(tmpRepo.toPath())) {
            File originalRepo = Path.of(gitStorage, owner, repo).toFile();

            boolean originAlreadyInit;
            try (Git origin = Git.open(originalRepo)) {
                originAlreadyInit = !origin.branchList().call().isEmpty();

            } catch (Exception e) {
                log.error("error fetching branch list of remote {}/{}", owner, repo, e);
                originAlreadyInit = false;
            }

            if (originAlreadyInit) {
                Git.cloneRepository()
                    .setURI(originalRepo.toURI().toString())
                    .setDirectory(tmpRepo)
                    .setBranchesToClone(Collections.singleton("refs/heads/" + branch))
                    .setBranch(branch)
                    .call()
                    .close();
            } else {
                Git.cloneRepository()
                    .setURI(originalRepo.toURI().toString())
                    .setDirectory(tmpRepo)
                    .setBranch(branch)
                    .call()
                    .close();
            }
        }

        try (Git git = Git.open(tmpRepo)) {
            git.checkout().setName(branch).call();
            ObjectId commitId = ObjectId.fromString(toSha);
            RevWalk revWalk = new RevWalk(git.getRepository());
            RevCommit commit = revWalk.parseCommit(commitId);
            git.checkout().addPath(".").setStartPoint(commit).call();
            git.add().addFilepattern(".").call();
            git.commit()
                .setAllowEmpty(false)
                .setMessage("Rollback to " + toSha)
                .setCommitter(committerName, committerEmail)
                .call();
            gitMetaService.recordContributor(owner, repo, committerName);

            String refSpec = git.getRepository().getBranch() + ":" + branch;
            log.info("pushing to remote, spec={}", refSpec);
            git.push()
                .setRemote("origin")
                .setRefSpecs(new RefSpec(refSpec))
                .call();

            updateCaches(owner, repo);
        }
    }

    @Override
    @SneakyThrows
    public List<BriefCommitDTO> listCommits(String owner, String repo, String branchOrCommit) {
        File repoFs = selectCache(owner, repo);
        try (Git git = Git.open(repoFs)) {
            cacheCheckout(git, branchOrCommit);

            List<BriefCommitDTO> res = new ArrayList<>();
            git.log()
                .call()
                .forEach(commit -> {
                    var brief = BriefCommitDTO.builder()
                        .id(commit.getId().getName())
                        .user(commit.getAuthorIdent().getName())
                        .ts(commit.getCommitTime())
                        .label(commit.getShortMessage())
                        .build();
                    res.add(brief);
                });
            return res;
        }
    }

    @Override
    @SneakyThrows
    public Result branchDelete(String owner, String repo, String initBranch) {
        try (Git git = Git.open(Path.of(gitStorage, owner, repo).toFile())) {
            git.branchDelete()
                .setBranchNames(initBranch)
                .setForce(true)
                .call();
        } catch (GitAPIException e) {
            log.error("error deleting branch", e);
            throw e;
        }
        updateCaches(owner, repo);
        return Result.success();
    }

    @Override
    @SneakyThrows
    public Result branchCheckoutB(String owner, String repo, String fromBranch, String toBranch) {
        try (Git git = Git.open(Path.of(gitStorage, owner, repo).toFile())) {
            git.branchCreate()
                .setName(toBranch)
                .setStartPoint(fromBranch)
                .call();
        } catch (GitAPIException e) {
            log.error("error deleting branch", e);
            throw e;
        }
        updateCaches(owner, repo);
        return Result.success();
    }

    @Override
    @Nullable
    @SneakyThrows
    public LatestCommitDTO latestCommitOfCurrentRepo(String owner, String repo,
                                                     String branchOrCommit,
                                                     @Nullable String relPath) {
        File repoFs = selectCache(owner, repo);
        try (Git git = Git.open(repoFs)) {
            cacheCheckout(git, branchOrCommit);

            Iterator iter;
            if (relPath == null || relPath.strip().length() == 0) {
                iter = git.log()
                    .call()
                    .iterator();
            } else {
                iter = git.log()
                    .addPath(relPath)
                    .call()
                    .iterator();
            }
            int cnt = 0;
            while (iter.hasNext()) {
                cnt++;
                iter.next();
            }

            LatestCommitInfo info;
            if (relPath == null || relPath.strip().length() == 0) {
                info = new LatestCommitInfo(git, null);
            } else {
                info = new LatestCommitInfo(git, new File(relPath));
            }

            return LatestCommitDTO.builder()
                .latest(info)
                .cnt(cnt)
                .build();
        }
    }
}
