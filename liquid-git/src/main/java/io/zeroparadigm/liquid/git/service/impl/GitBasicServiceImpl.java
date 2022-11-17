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
import io.zeroparadigm.liquid.git.exceptions.RepositoryAlreadyExistsException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GitBasicServiceImpl implements GitBasicService {

    @Value("${storage.git}")
    private String gitStorage;

    @Override
    public void createRepo(String owner, String repo, String initBranch) throws IOException, GitAPIException {
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
}
