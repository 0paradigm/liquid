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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Future;
import javax.annotation.PostConstruct;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class GitWebServiceImpl implements GitWebService {

    @Value("${storage.git}")
    private String gitStorage;

    @Value("${storage.git-tmp}")
    private String gitTmpStorage;

    private static final String TMPDIR_PATTERN = "%s-%s";

    @PostConstruct
    void deleteWorkDirOnJvmExit() {
        new File(gitTmpStorage).deleteOnExit();
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
            Git.cloneRepository()
                    .setURI(originalRepo.toURI().toString())
                    .setDirectory(tmpRepo)
                    .setBranchesToClone(Collections.singleton(fromBranch))
                    .setBranch(fromBranch)
                    .call()
                    .close();
        }

        Path absPath = Path.of(tmpRepo.toString(), relPath);
        Files.createDirectories(absPath);
        Path dest = Path.of(tmpRepo.toString(), relPath, file.getOriginalFilename());
        log.debug("transforming uploaded file '{}' to fs://{}", file.getOriginalFilename(), dest);
        file.transferTo(dest);
        return Path.of(relPath, file.getOriginalFilename()).toString().substring(2);
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
            try {
                git.checkout()
                        .setName(toBranch)
                        .call();
            } catch (RefNotFoundException e) {
                log.info("Creating new branch '{}'", toBranch);
                git.checkout()
                        .setCreateBranch(true)
                        .setOrphan(true)
                        .setName(toBranch)
                        .call();
            }

            if (Objects.isNull(addFiles)) {
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

            git.push().call();
            deleteDirAsync(tmpRepo);
        }
    }

    @Override
    public List<LatestCommitInfo> listFiles(String owner, String repo, String branchOrCommit,
                                            @Nullable String relPath) throws IOException, GitAPIException {
        File repoFs = Path.of(gitStorage, owner, repo, Objects.requireNonNullElse(relPath, "")).toFile();
        Path repoRoot = Path.of(gitStorage, owner, repo);

        try (Git git = Git.open(repoFs)) {
            git.checkout()
                    .setName(branchOrCommit)
                    .call();

            return Arrays.stream(Objects.requireNonNullElse(repoFs.listFiles(), new File[0])).parallel()
                    .map(f -> repoRoot.relativize(f.toPath()))
                    .map(Path::toFile)
                    .filter(f -> !".git".equals(f.getName()))
                    .map(f -> new LatestCommitInfo(git, f))
                    .toList();
        }
    }

    @Override
    @Nullable
    public RevCommit latestCommitOfCurrentRepo(String owner, String repo, String branchOrCommit,
                                               @Nullable String relPath) throws IOException, GitAPIException {
        File repoFs = Path.of(gitStorage, owner, repo, Objects.requireNonNullElse(relPath, "")).toFile();

        try (Git git = Git.open(repoFs)) {
            git.checkout()
                    .setName(branchOrCommit)
                    .call();

            return git.log()
                    .addPath(relPath)
                    .setMaxCount(1)
                    .call()
                    .iterator()
                    .next();
        }
    }
}
