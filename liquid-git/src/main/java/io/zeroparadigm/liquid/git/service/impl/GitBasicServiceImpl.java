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
import org.apache.commons.lang3.StringUtils;
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
    public void deleteRepo(String owner, String repo) {
        File repoDir = Path.of(gitStorage, owner, repo).toFile();
        FileUtils.deleteQuietly(repoDir);
    }

    @Override
    public void renameRepo(String owner, String repo, String newRepoName) {
        // caches are left over
        File repoDir = Path.of(gitStorage, owner, repo).toFile();
        File newRepoDir = Path.of(gitStorage, owner, newRepoName).toFile();
        repoDir.renameTo(newRepoDir);
    }

    @Override
    public void addReadMe(String owner, String repo, String desc) {
        Path repoPath = Path.of(gitStorage, owner, repo);
        if (desc.trim().length() == 0) {
            desc = "Hosted by Liquid";
        }
        String ctx = StringUtils.trim(String.format("""
            # %s
                        
            %s
            """, repo, desc));
        try {
            MultipartFile mfile = createMfileByPath(repoPath.toString() + "/README.md", ctx);
            gitWebService.uploadFile(owner, repo, "master", mfile, "", "-1");
        } catch (Exception e) {
            log.error("error creating repo", e);
        }
    }

    @Override
    public void addGitIgnore(String owner, String repo, String lang) {
        String ctx = switch (lang) {
            case "C" -> """
                # Prerequisites
                *.d
                                
                # Object files
                *.o
                *.ko
                *.obj
                *.elf
                                
                # Linker output
                *.ilk
                *.map
                *.exp
                                
                # Precompiled Headers
                *.gch
                *.pch
                                
                # Libraries
                *.lib
                *.a
                *.la
                *.lo
                                
                # Shared objects (inc. Windows DLLs)
                *.dll
                *.so
                *.so.*
                *.dylib
                                
                # Executables
                *.exe
                *.out
                *.app
                *.i*86
                *.x86_64
                *.hex
                                
                # Debug files
                *.dSYM/
                *.su
                *.idb
                *.pdb
                                
                # Kernel Module Compile Results
                *.mod*
                *.cmd
                .tmp_versions/
                modules.order
                Module.symvers
                Mkfile.old
                dkms.conf
                """;
            case "C++" -> """
                # Prerequisites
                *.d
                                
                # Compiled Object files
                *.slo
                *.lo
                *.o
                *.obj
                                
                # Precompiled Headers
                *.gch
                *.pch
                                
                # Compiled Dynamic libraries
                *.so
                *.dylib
                *.dll
                                
                # Fortran module files
                *.mod
                *.smod
                                
                # Compiled Static libraries
                *.lai
                *.la
                *.a
                *.lib
                                
                # Executables
                *.exe
                *.out
                *.app
                """;
            case "Java" -> """
                # Compiled class file
                *.class
                                
                # Log file
                *.log
                                
                # BlueJ files
                *.ctxt
                                
                # Mobile Tools for Java (J2ME)
                .mtj.tmp/
                                
                # Package Files #
                *.jar
                *.war
                *.nar
                *.ear
                *.zip
                *.tar.gz
                *.rar
                                
                # virtual machine crash logs, see http://www.java.com/en/download/help/error_hotspot.xml
                hs_err_pid*
                replay_pid*
                """;
            case "Python" -> """
                # Byte-compiled / optimized / DLL files
                __pycache__/
                *.py[cod]
                *$py.class
                                
                # C extensions
                *.so
                                
                # Distribution / packaging
                .Python
                build/
                develop-eggs/
                dist/
                downloads/
                eggs/
                .eggs/
                lib/
                lib64/
                parts/
                sdist/
                var/
                wheels/
                share/python-wheels/
                *.egg-info/
                .installed.cfg
                *.egg
                MANIFEST
                                
                # PyInstaller
                #  Usually these files are written by a python script from a template
                #  before PyInstaller builds the exe, so as to inject date/other infos into it.
                *.manifest
                *.spec
                                
                # Installer logs
                pip-log.txt
                pip-delete-this-directory.txt
                                
                # Unit test / coverage reports
                htmlcov/
                .tox/
                .nox/
                .coverage
                .coverage.*
                .cache
                nosetests.xml
                coverage.xml
                *.cover
                *.py,cover
                .hypothesis/
                .pytest_cache/
                cover/
                                
                # Translations
                *.mo
                *.pot
                                
                # Django stuff:
                *.log
                local_settings.py
                db.sqlite3
                db.sqlite3-journal
                                
                # Flask stuff:
                instance/
                .webassets-cache
                                
                # Scrapy stuff:
                .scrapy
                                
                # Sphinx documentation
                docs/_build/
                                
                # PyBuilder
                .pybuilder/
                target/
                                
                # Jupyter Notebook
                .ipynb_checkpoints
                                
                # IPython
                profile_default/
                ipython_config.py
                                
                # pyenv
                #   For a library or package, you might want to ignore these files since the code is
                #   intended to run in multiple environments; otherwise, check them in:
                # .python-version
                                
                # pipenv
                #   According to pypa/pipenv#598, it is recommended to include Pipfile.lock in version control.
                #   However, in case of collaboration, if having platform-specific dependencies or dependencies
                #   having no cross-platform support, pipenv may install dependencies that don't work, or not
                #   install all needed dependencies.
                #Pipfile.lock
                                
                # poetry
                #   Similar to Pipfile.lock, it is generally recommended to include poetry.lock in version control.
                #   This is especially recommended for binary packages to ensure reproducibility, and is more
                #   commonly ignored for libraries.
                #   https://python-poetry.org/docs/basic-usage/#commit-your-poetrylock-file-to-version-control
                #poetry.lock
                                
                # pdm
                #   Similar to Pipfile.lock, it is generally recommended to include pdm.lock in version control.
                #pdm.lock
                #   pdm stores project-wide configurations in .pdm.toml, but it is recommended to not include it
                #   in version control.
                #   https://pdm.fming.dev/#use-with-ide
                .pdm.toml
                                
                # PEP 582; used by e.g. github.com/David-OConnor/pyflow and github.com/pdm-project/pdm
                __pypackages__/
                                
                # Celery stuff
                celerybeat-schedule
                celerybeat.pid
                                
                # SageMath parsed files
                *.sage.py
                                
                # Environments
                .env
                .venv
                env/
                venv/
                ENV/
                env.bak/
                venv.bak/
                                
                # Spyder project settings
                .spyderproject
                .spyproject
                                
                # Rope project settings
                .ropeproject
                                
                # mkdocs documentation
                /site
                                
                # mypy
                .mypy_cache/
                .dmypy.json
                dmypy.json
                                
                # Pyre type checker
                .pyre/
                                
                # pytype static type analyzer
                .pytype/
                                
                # Cython debug symbols
                cython_debug/
                                
                # PyCharm
                #  JetBrains specific template is maintained in a separate JetBrains.gitignore that can
                #  be found at https://github.com/github/gitignore/blob/main/Global/JetBrains.gitignore
                #  and can be added to the global gitignore or merged into this file.  For a more nuclear
                #  option (not recommended) you can uncomment the following to ignore the entire idea folder.
                #.idea/
                """;
            default -> "";
        };
        log.error("create gitignore of " + lang);
        System.out.println(ctx);
        Path repoPath = Path.of(gitStorage, owner, repo);
        try {
            MultipartFile mfile = createMfileByPath(repoPath + "/.gitignore", ctx);
            gitWebService.uploadFile(owner, repo, "master", mfile, "", "-1");
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

    public static MultipartFile createMfileByPath(String path, String ctx) {
        MultipartFile mFile = null;
        try {
            File file = new File(path);
            file.createNewFile();
            FileUtils.writeStringToFile(file, ctx, "UTF-8");

            FileInputStream fileInputStream = new FileInputStream(file);
            String fileName = path.substring((path.lastIndexOf("/") + 1));
            mFile = new MockMultipartFile(fileName, fileName,
                ContentType.TEXT_PLAIN.getMimeType(), fileInputStream);
        } catch (Exception e) {
            log.error("Create multipart file error ：{}", e);
        }
        return mFile;
    }
}
