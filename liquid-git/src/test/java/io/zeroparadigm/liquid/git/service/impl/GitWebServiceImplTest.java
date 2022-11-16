package io.zeroparadigm.liquid.git.service.impl;


import io.zeroparadigm.liquid.git.DubboMockFactory;
import io.zeroparadigm.liquid.git.LiquidGit;
import io.zeroparadigm.liquid.git.exceptions.RepositoryAlreadyExistsException;
import io.zeroparadigm.liquid.git.service.GitWebService;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = {LiquidGit.class, DubboMockFactory.class})
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "storage.git = storage-sa/git",
        "storage.git-tmp = storage-sa/git-tmp",
        "storage.git-bkp = storage-sa/git-bkp",
})
@DirtiesContext
@ComponentScan(basePackages = {"io.zeroparadigm.liquid.git.service.impl"})
class GitWebServiceImplTest {

    @Autowired
    GitWebService gitWebService;

    @Autowired
    GitBasicServiceImpl gitBasicService;

    @Value("${storage.git}")
    private String gitStorage;

    private static final String storageRoot = "storage-sa";

    @SneakyThrows
    @BeforeEach
    @Order(1)
    void removeAllRepos() {
        File storage = Path.of(gitStorage).toFile();
        FileUtils.deleteDirectory(storage);
        FileUtils.createParentDirectories(storage);
    }

    @BeforeEach
    @Order(2)
    void createSaRepo() throws Exception {
        gitBasicService.createRepo("liquid", "sa", "main");
    }

    @SneakyThrows
    @AfterAll
    static void removeSaStorage() {
        File storage = Path.of(storageRoot).toFile();
        FileUtils.deleteDirectory(storage);
    }

    @Test
    void testUploadStartTask() throws Exception {
        String ts = String.valueOf(System.currentTimeMillis());
        gitWebService.uploadFile("liquid", "sa", "main",
                new MockMultipartFile("README.md", "README.md", null, "sa".getBytes(StandardCharsets.UTF_8)),
                "/docs", ts);

        var tmpRepo = Path.of("storage-sa/git-tmp", "liquid", "sa-" + ts).toFile();
        assertThat(FileUtils.isDirectory(tmpRepo) && !FileUtils.isEmptyDirectory(tmpRepo)).isTrue();

        var docsPath = Path.of(tmpRepo.getPath(), "docs").toFile();
        assertThat(FileUtils.isDirectory(docsPath) && !FileUtils.isEmptyDirectory(docsPath)).isTrue();
    }

    @Test
    void testUploadFilesInTask() throws Exception {
        String ts = String.valueOf(System.currentTimeMillis());
        gitWebService.uploadFile("liquid", "sa", "main",
                new MockMultipartFile("README.md", "README.md", null, "sa".getBytes(StandardCharsets.UTF_8)),
                "/docs", ts);
        gitWebService.uploadFile("liquid", "sa", "main",
                new MockMultipartFile("README2.md", "README2.md", null, "sa2".getBytes(StandardCharsets.UTF_8)),
                "/docs", ts);

        var tmpRepo = Path.of("storage-sa/git-tmp", "liquid", "sa-" + ts).toFile();
        assertThat(FileUtils.isDirectory(tmpRepo) && !FileUtils.isEmptyDirectory(tmpRepo)).isTrue();

        var docsPath = Path.of(tmpRepo.getPath(), "docs").toFile();
        assertThat(Files.list(docsPath.toPath()).count()).isEqualTo(2);
    }

    @Test
    void testCommitInvalidTask() throws Exception {
        String ts = String.valueOf(System.currentTimeMillis());
        assertThatThrownBy(() -> gitWebService.commit("liquid", "sa", "main", ts, null, "no temp repo"))
                .isInstanceOf(RepositoryNotFoundException.class);
    }

    @Test
    void testCommitSample() throws Exception {
        String ts = String.valueOf(System.currentTimeMillis());
        gitWebService.uploadFile("liquid", "sa", "main",
                new MockMultipartFile("README.md", "README.md", null, "sa".getBytes(StandardCharsets.UTF_8)),
                "/docs", ts);
        assertThatNoException()
                .isThrownBy(() -> gitWebService.commit("liquid", "sa", "main", ts, null, "no temp repo"));

        System.out.println(gitWebService.listFiles("liquid", "sa", "main", "."));
    }
}
