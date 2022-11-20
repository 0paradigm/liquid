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

import io.zeroparadigm.liquid.git.pojo.LatestCommitInfo;
import io.zeroparadigm.liquid.git.service.GitWebService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.BranchConfig;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.RefSpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Min;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Slf4j
@Service
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
                       @Nullable List<String> addFiles, String message) throws IOException, GitAPIException {

        // TODO: verify auth, get user(committer) info
        String committerName = "aaa";
        String committerEmail = "";

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

    private synchronized File selectCache(String owner, String repo) {
        for (int i = 0; ; i++, i %= cacheObjNum) {
            File cacheRepo = Path.of(gitCacheStorage, owner, String.format("%s-%d", repo, i)).toFile();
            if (!Files.exists(cacheRepo.toPath())) {
                createCache(owner, repo);
                continue;
            }
            if (Files.exists(Path.of(cacheRepo.getPath(), ".git", "index.lock"))) {
                log.warn("cache {} is busy", i);
                continue;
            }
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
            File cacheRepo = Path.of(gitCacheStorage, owner, String.format("%s-%d", repo, i)).toFile();
            FileUtils.deleteQuietly(cacheRepo);
            FileUtils.copyDirectory(dest0, cacheRepo);
        }
    }

    private void updateCaches(String owner, String repo) {
        for (int i = 0; i < cacheObjNum; i++) {
            File cacheRepo = Path.of(gitCacheStorage, owner, String.format("%s-%d", repo, i)).toFile();
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
        File repoRoot = selectCache(owner, repo);
        File repoFs = Path.of(repoRoot.getPath(), Objects.requireNonNullElse(relPath, "")).toFile();

        if (!repoFs.exists()) {
            throw new FileNotFoundException(repoFs.getCanonicalPath());
        }
        try (Git git = Git.open(repoRoot)) {
            cacheCheckout(git, branchOrCommit);

            return Arrays.stream(Objects.requireNonNullElse(repoFs.listFiles(), new File[0])).parallel()
                    .map(f -> repoRoot.toPath().relativize(f.toPath()))
                    .map(Path::toFile)
                    .filter(f -> !".git".equals(f.getName()))
                    .map(f -> new LatestCommitInfo(git, f))
                    .toList();
        }
    }

    @Override
    public byte[] getFile(String owner, String repo, String branchOrCommit, String filePath) throws IOException, GitAPIException {
        File repoRoot = selectCache(owner, repo);

        try (Git git = Git.open(repoRoot)) {
            cacheCheckout(git, branchOrCommit);

            File file = new File(repoRoot, filePath);
//            DiskFileItem fileItem = new DiskFileItem("mainFile", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());

            try (
                    InputStream input = new FileInputStream(file);
//                OutputStream os = fileItem.getOutputStream()
            ) {
//                IOUtils.copy(input, os);
                return input.readAllBytes();
            }
        }
    }

    @Override
    @Nullable
    public RevCommit latestCommitOfCurrentRepo(String owner, String repo, String branchOrCommit,
                                               @Nullable String relPath) throws IOException, GitAPIException {
//        File repoFs = Path.of(gitStorage, owner, repo, Objects.requireNonNullElse(relPath, "")).toFile();
//
//        try (Git git = Git.open(repoFs)) {
//            git.checkout()
//                    .setName(branchOrCommit)
//                    .call();
//
//            return git.log()
//                    .addPath(relPath)
//                    .setMaxCount(1)
//                    .call()
//                    .iterator()
//                    .next();
//        }
        // fetch database to find out the real user
        return null;
    }
}
