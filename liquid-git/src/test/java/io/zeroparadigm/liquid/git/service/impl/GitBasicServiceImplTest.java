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

import io.zeroparadigm.liquid.git.DubboMockFactory;
import io.zeroparadigm.liquid.git.LiquidGit;
import io.zeroparadigm.liquid.git.exceptions.RepositoryAlreadyExistsException;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = {LiquidGit.class, DubboMockFactory.class})
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "storage.git = storage-sa/git",
        "storage.git-tmp = storage-sa/git-tmp",
        "storage.git-cache = storage-sa/git-cache",
        "git.cache-num = 3",
})
class GitBasicServiceImplTest {

    @Autowired
    GitBasicServiceImpl gitBasicService;

    @Value("${storage.git}")
    private String gitStorage;

    private static final String storageRoot = "storage-sa";

    @SneakyThrows
    @BeforeEach
    void removeAllRepos() {
        File storage = Path.of(gitStorage).toFile();
        FileUtils.deleteDirectory(storage);
        FileUtils.createParentDirectories(storage);
    }

    @SneakyThrows
    @AfterAll
    static void removeSaStorage() {
        File storage = Path.of(storageRoot).toFile();
        FileUtils.deleteDirectory(storage);
    }

    @Test
    void testCreateRepoSimple() throws Exception {
        gitBasicService.createRepo("liquid", "sa", "main");
        Path repoPath = Path.of(gitStorage, "liquid", "sa");

        assertThat(FileUtils.isDirectory(repoPath.toFile())).isTrue();
        assertThat(FileUtils.isEmptyDirectory(repoPath.toFile())).isFalse();

        try (Git git = Git.open(repoPath.toFile())) {
            assertThat(git.getRepository().getBranch()).isEqualTo("main");
        } catch (Exception e) {
            fail(String.valueOf(e));
        }
    }

    @Test
    void testCreateAlreadyExistsRepo() throws Exception {
        assertThatNoException().isThrownBy(() -> gitBasicService.createRepo("liquid", "sa", "main"));
        assertThatThrownBy(() -> gitBasicService.createRepo("liquid", "sa", "main"))
                .isInstanceOf(RepositoryAlreadyExistsException.class);
    }
}
