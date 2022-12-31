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

import io.zeroparadigm.liquid.common.api.git.GitBasicService;
import io.zeroparadigm.liquid.common.bo.UserBO;
import io.zeroparadigm.liquid.git.exceptions.RepositoryAlreadyExistsException;
import io.zeroparadigm.liquid.git.service.GitWebService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.http.entity.ContentType;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@DubboService
public class GitBasicServiceImpl implements GitBasicService {

    @Value("${storage.git}")
    private String gitStorage;

    @Autowired
    private GitWebService gitWebService;

    @Override
    public void createRepo(String owner, String repo, String initBranch)
        throws IOException, GitAPIException {
        Path repoPath = Path.of(gitStorage, owner, repo);
        if (Files.exists(repoPath)) {
            throw new RepositoryAlreadyExistsException(String.format("%s/%s", owner, repo));
        }
        Files.createDirectories(repoPath);
        try (
            Git git = Git.init()
                .setBare(true)
                .setDirectory(repoPath.toFile())
                .setInitialBranch(initBranch)
                .call()) {
            log.info("repo created: {}", git.toString());
        } catch (Exception e) {
            log.error("error creating repo", e);
            FileUtils.deleteQuietly(repoPath.toFile());
            throw e;
        }
    }

    @Override
    public void addReadMe(String owner, String repo, String initBranch) {
        Path repoPath = Path.of(gitStorage, owner, repo);
        try {
            MultipartFile mfile = createMfileByPath(repoPath.toString() + "/README.md");
            gitWebService.uploadFile(owner, repo, initBranch, mfile, "", "-1");
        } catch (Exception e) {
            log.error("error creating repo", e);
        }
    }

    @Override
    public void addGitIgnore(String owner, String repo, String initBranch) {
        Path repoPath = Path.of(gitStorage, owner, repo);
        try {
            MultipartFile mfile = createMfileByPath(repoPath.toString() + "/.gitignore");
            gitWebService.uploadFile(owner, repo, initBranch, mfile, "", "-1");
        } catch (Exception e) {
            log.error("error creating repo", e);
        }
    }

    @Override
    public void webCommit(String owner, String repo, String initBranch, List<String> addFiles,
                          UserBO committer) {
        try {
            gitWebService.commit(owner, repo, initBranch, "-1", addFiles, "Initial commit",
                committer);
        } catch (Exception e) {
            log.error("error creating repo", e);
        }
    }

    @Override
    @SneakyThrows
    public List<String> listBranches(String owner, String repo) {
        Path repoPath = Path.of(gitStorage, owner, repo);
        List<String> res = new LinkedList<>();
        try (Git git = Git.open(repoPath.toFile())) {
            git.branchList().call().forEach(bi -> res.add(bi.getName().replace("refs/heads/", "")));
        }
        return res;
    }

    @Override
    public void forkRepo(String fromOwner, String fromRepo, String toOwner, String toRepo)
        throws IOException, GitAPIException {
        Path repoPath = Path.of(gitStorage, toOwner, toRepo);
        if (Files.exists(repoPath)) {
            throw new RepositoryAlreadyExistsException(String.format("%s/%s", toOwner, toRepo));
        }
        Files.createDirectories(repoPath);
        try (
            Git git = Git.cloneRepository()
                .setBare(true)
                .setURI(Path.of(gitStorage, fromOwner, fromRepo).toString())
                .setDirectory(repoPath.toFile())
                .call()) {
            log.info("repo forked: {}", git.toString());
        } catch (Exception e) {
            log.error("error forking repo", e);
            FileUtils.deleteQuietly(repoPath.toFile());
            throw e;
        }
    }

    public static MultipartFile createMfileByPath(String path) {
        MultipartFile mFile = null;
        try {
            File file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);

            String fileName = file.getName();
            fileName = fileName.substring((fileName.lastIndexOf("/") + 1));
            mFile = new MockMultipartFile(fileName, fileName,
                ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);
        } catch (Exception e) {
            log.error("Create multipart file error ：{}", e);
//            e.printStackTrace();
        }
        return mFile;
    }
}
