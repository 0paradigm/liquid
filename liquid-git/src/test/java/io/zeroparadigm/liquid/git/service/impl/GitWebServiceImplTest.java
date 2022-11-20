package io.zeroparadigm.liquid.git.service.impl;


import io.zeroparadigm.liquid.git.DubboMockFactory;
import io.zeroparadigm.liquid.git.LiquidGit;
import io.zeroparadigm.liquid.git.service.GitWebService;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.assertj.core.groups.Tuple;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.EmptyCommitException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.internal.storage.dfs.DfsRepositoryDescription;
import org.eclipse.jgit.internal.storage.dfs.InMemoryRepository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.util.FS;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = {LiquidGit.class, DubboMockFactory.class})
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "storage.git = storage-sa/git",
        "storage.git-tmp = storage-sa/git-tmp",
        "storage.git-cache = storage-sa/git-cache",
        "git.cache-num = 3",
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

    @Value("${storage.git-cache}")
    private String gitCache;

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
    void testCommitToNewRepo() throws Exception {
        String ts = String.valueOf(System.currentTimeMillis());
        gitWebService.uploadFile("liquid", "sa", "main",
                new MockMultipartFile("README.md", "README.md", null, "sa".getBytes(StandardCharsets.UTF_8)),
                "/docs", ts);
        assertThatNoException()
                .isThrownBy(() -> gitWebService.commit("liquid", "sa", "dev", ts, null, "no temp repo"));
        Set<String> branches = Git.lsRemoteRepository()
                .setRemote(Path.of(gitStorage, "liquid", "sa").toFile().getPath())
                .call()
                .stream()
                .map(Ref::getName)
                .collect(Collectors.toSet());
        assertThat(branches).containsExactly("refs/heads/dev");
    }

    @Test
    void testCommitSimple() throws Exception {
        String ts = String.valueOf(System.currentTimeMillis());
        gitWebService.uploadFile("liquid", "sa", "main",
                new MockMultipartFile("README.md", "README.md", null, "sa".getBytes(StandardCharsets.UTF_8)),
                "/docs", ts);
        assertThatNoException()
                .isThrownBy(() -> gitWebService.commit("liquid", "sa", "dev", ts, null, "aaa"));

        gitWebService.uploadFile("liquid", "sa", "dev",
                new MockMultipartFile("README.rst", "README.rst", null, "sa".getBytes(StandardCharsets.UTF_8)),
                "/docs", ts);
        assertThatNoException()
                .isThrownBy(() -> gitWebService.commit("liquid", "sa", "dev", ts, null, "bbb"));

        Set<String> branches = Git.lsRemoteRepository()
                .setRemote(Path.of(gitStorage, "liquid", "sa").toFile().getPath())
                .call()
                .stream()
                .map(Ref::getName)
                .collect(Collectors.toSet());
        assertThat(branches).containsExactly("refs/heads/dev");
    }

    @Test
    void testCommitNoChange() throws Exception {
        String ts = String.valueOf(System.currentTimeMillis());
        gitWebService.uploadFile("liquid", "sa", "main",
                new MockMultipartFile("README.md", "README.md", null, "sa".getBytes(StandardCharsets.UTF_8)),
                "/docs", ts);
        assertThatNoException()
                .isThrownBy(() -> gitWebService.commit("liquid", "sa", "dev", ts, null, "aaa"));

        gitWebService.uploadFile("liquid", "sa", "dev",
                new MockMultipartFile("README.md", "README.md", null, "sa".getBytes(StandardCharsets.UTF_8)),
                "/docs", ts);
        assertThatThrownBy(() -> gitWebService.commit("liquid", "sa", "dev", ts, null, "bbb"))
                .isInstanceOf(EmptyCommitException.class);
    }

    @Test
    void testCacheBranches() throws Exception {
        String ts = String.valueOf(System.currentTimeMillis());
        gitWebService.uploadFile("liquid", "sa", "main",
                new MockMultipartFile("README.md", "README.md", null, "sa".getBytes(StandardCharsets.UTF_8)),
                "/docs", ts);
        assertThatNoException()
                .isThrownBy(() -> gitWebService.commit("liquid", "sa", "dev", ts, null, "aaa"));

        gitWebService.uploadFile("liquid", "sa", "dev",
                new MockMultipartFile("README1.md", "README1.md", null, "sa".getBytes(StandardCharsets.UTF_8)),
                "/docs", ts);
        assertThatNoException()
                .isThrownBy(() -> gitWebService.commit("liquid", "sa", "dev1", ts, null, "bbb"));

        File cache0 = Path.of(gitCache, "liquid", "sa-0").toFile();
        try (Git cache = Git.open(cache0)) {
            Set<String> branches = cache.branchList()
                    .setListMode(ListBranchCommand.ListMode.ALL)
                    .call()
                    .stream()
                    .map(Ref::getName)
                    .collect(Collectors.toSet());
            assertThat(branches).contains("refs/remotes/origin/dev", "refs/remotes/origin/dev1");
            assertThatNoException()
                    .isThrownBy(() -> cache.checkout()
                            .setForced(true)
                            .setCreateBranch(true)
                            .setName("dev1")
                            .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                            .setStartPoint("origin/dev1")
                            .call());
        }
    }

    @Test
    void testListFiles() throws Exception {
        String ts = String.valueOf(System.currentTimeMillis());
        gitWebService.uploadFile("liquid", "sa", "main",
                new MockMultipartFile("README.md", "README.md", null, "sa".getBytes(StandardCharsets.UTF_8)),
                "/docs", ts);
        assertThatNoException()
                .isThrownBy(() -> gitWebService.commit("liquid", "sa", "dev", ts, null, "aaa"));

        gitWebService.uploadFile("liquid", "sa", "dev",
                new MockMultipartFile("README1.md", "README1.md", null, "sa".getBytes(StandardCharsets.UTF_8)),
                "/docs", ts);
        assertThatNoException()
                .isThrownBy(() -> gitWebService.commit("liquid", "sa", "dev1", ts, null, "bbb"));

        assertThat(gitWebService.listFiles("liquid", "sa", "dev", null))
                .extracting("name", "isDir", "message")
                .containsExactly(
                        Tuple.tuple("docs", true, "aaa")
                );
        assertThat(gitWebService.listFiles("liquid", "sa", "dev", "docs"))
                .extracting("name", "isDir", "message")
                .containsExactly(
                        Tuple.tuple("README.md", false, "aaa")
                );
        assertThat(gitWebService.listFiles("liquid", "sa", "dev1", "docs"))
                .extracting("name", "isDir", "message")
                .containsExactly(
                        Tuple.tuple("README.md", false, "aaa"),
                        Tuple.tuple("README1.md", false, "bbb")
                );
    }

    @Test
    void testListFilesFakeConcurrent() throws Exception {
        String ts = String.valueOf(System.currentTimeMillis());
        gitWebService.uploadFile("liquid", "sa", "main",
                new MockMultipartFile("README.md", "README.md", null, "sa".getBytes(StandardCharsets.UTF_8)),
                "/docs", ts);
        gitWebService.commit("liquid", "sa", "dev", ts, null, "aaa");
        gitWebService.uploadFile("liquid", "sa", "dev",
                new MockMultipartFile("README1.md", "README1.md", null, "sa".getBytes(StandardCharsets.UTF_8)),
                "/docs", ts);
        gitWebService.commit("liquid", "sa", "dev1", ts, null, "bbb");

        // this actually cannot hold for multiple users accessing the same repo
        // however it does speed up for file listing for single user when switching dir/branch
        assertThatNoException()
                .isThrownBy(() -> {
                    // using parallel stream will break it
                    Stream.of("dev", "dev1", "dev", "dev1", "dev", "dev1", "dev", "dev1")
                            .forEach(branch -> {
                                try {
                                    gitWebService.listFiles("liquid", "sa", branch, "docs");
                                } catch (IOException | GitAPIException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                });
    }
}
