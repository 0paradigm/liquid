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
        "storage.git-bkp = storage-sa/git-bkp",
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
        Path repoGitDB = Path.of(gitStorage, "liquid", "sa", ".git");

        assertThat(FileUtils.isDirectory(repoPath.toFile())).isTrue();
        assertThat(FileUtils.isDirectory(repoGitDB.toFile())).isTrue();
        assertThat(FileUtils.isEmptyDirectory(repoGitDB.toFile())).isFalse();

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
