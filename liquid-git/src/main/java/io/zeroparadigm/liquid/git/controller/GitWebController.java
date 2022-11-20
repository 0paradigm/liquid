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

package io.zeroparadigm.liquid.git.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.zeroparadigm.liquid.common.api.git.GitBasicService;
import io.zeroparadigm.liquid.common.dto.Result;
import io.zeroparadigm.liquid.common.enums.ServiceStatus;
import io.zeroparadigm.liquid.common.exceptions.annotations.WrapsException;
import io.zeroparadigm.liquid.git.dto.WebCommitDTO;
import io.zeroparadigm.liquid.git.dto.WebCreateRepoDTO;
import io.zeroparadigm.liquid.git.pojo.LatestCommitInfo;
import io.zeroparadigm.liquid.git.service.GitWebService;

import java.util.List;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Git operations for website.
 *
 * @author hezean
 */
@Api
@RestController
@RequestMapping("/web")
@Slf4j
public class GitWebController {

    @Autowired
    private GitWebService gitWebService;

    @Autowired
    private GitBasicService gitBasicService;

    @PostMapping("/upload/{owner}/{repo}/{branch}")
    @ApiOperation(value = "upload", notes = "upload files to commit")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "owner", paramType = "path", value = "owner of repo", required = true, dataTypeClass = String.class, example = "apache"),
            @ApiImplicitParam(name = "repo", paramType = "path", value = "repo name", required = true, dataTypeClass = String.class, example = "dubbo"),
            @ApiImplicitParam(name = "branch", paramType = "path", value = "base branch ref", required = true, dataTypeClass = String.class, example = "dev-2.x"),
            @ApiImplicitParam(name = "file", paramType = "form", value = "file", required = true, dataTypeClass = MultipartFile.class, example = "<binary>"),
            @ApiImplicitParam(name = "path", paramType = "form", value = "relative path from repo root, without filename", required = true, dataTypeClass = String.class, example = "docs"),
            @ApiImplicitParam(name = "taskId", paramType = "form", value = "task id", required = true, dataTypeClass = String.class, example = "17287390173"),
    })
    @SneakyThrows
    @WrapsException(ServiceStatus.GIT_WEB_UPLOAD_FAIL)
    @SuppressWarnings("rawtypes")
    public Result upload(@PathVariable String owner,
                         @PathVariable String repo,
                         @PathVariable String branch,
                         @RequestPart MultipartFile file,
                         @RequestPart(required = false) String path,
                         @RequestPart String taskId) {
        try {
            String addPath = gitWebService.uploadFile(owner, repo, branch, file, path, taskId);
            return Result.success(addPath);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping("/upload/{owner}/{repo}/{branch}/commit")
    @ApiOperation(value = "commitUpload", notes = "commit changes just uploaded")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "owner", paramType = "path", value = "owner of repo", required = true, dataTypeClass = String.class, example = "apache"),
            @ApiImplicitParam(name = "repo", paramType = "path", value = "repo name", required = true, dataTypeClass = String.class, example = "dubbo"),
            @ApiImplicitParam(name = "branch", paramType = "path", value = "target branch, may be newly created", required = true, dataTypeClass = String.class, example = "dev-2.x"),
            @ApiImplicitParam(name = "taskId", paramType = "form", value = "task id", required = true, dataTypeClass = String.class, example = "17287390173"),
            @ApiImplicitParam(name = "message", paramType = "form", value = "commit message", required = true, dataTypeClass = String.class, example = "feat: add an import feature"),
            @ApiImplicitParam(name = "addFiles", paramType = "form", value = "file list to 'git add', default '.'", required = false, dataTypeClass = List.class, example = "[\"README.md\", \"docs/intro.md\"]")
    })
    @SneakyThrows
    @WrapsException(ServiceStatus.GIT_WEB_COMMIT_FAIL)
    @SuppressWarnings("rawtypes")
    public Result commitUpload(@PathVariable String owner,
                               @PathVariable String repo,
                               @PathVariable String branch,
                               @RequestBody WebCommitDTO args) {
        gitWebService.commit(owner, repo, branch,
                args.getTaskId(), args.getAddFiles(), args.getMessage());
        return Result.success();
    }

    @PostMapping("/create/{owner}/{repo}")
    @ApiOperation(value = "create repo", notes = "create a bare repo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "owner", paramType = "path", value = "owner of repo", required = true, dataTypeClass = String.class, example = "apache"),
            @ApiImplicitParam(name = "repo", paramType = "path", value = "repo name", required = true, dataTypeClass = String.class, example = "dubbo"),
            @ApiImplicitParam(name = "args", paramType = "body", value = "{initBranch}", required = true, dataTypeClass = WebCreateRepoDTO.class),
    })
    @ResponseStatus(HttpStatus.CREATED)
    @SneakyThrows
    @WrapsException(ServiceStatus.GIT_REPO_ALREADY_EXISTS)
    @SuppressWarnings("rawtypes")
    public Result createRepo(@PathVariable String owner,
                             @PathVariable String repo,
                             @RequestBody WebCreateRepoDTO args) {
        gitBasicService.createRepo(owner, repo, args.getInitBranch());
        return Result.success();
    }

    @GetMapping("/list/{owner}/{repo}/{branchOrCommit}")
    @SneakyThrows
    @WrapsException(wrapped = ServiceStatus.NOT_FOUND, status = HttpStatus.NOT_FOUND)
    public Result<List<LatestCommitInfo>> listFiles(@PathVariable String owner, @PathVariable String repo,
                                                    @PathVariable String branchOrCommit,
                                                    @RequestParam(required = false) String relPath) {
        return Result.success(gitWebService.listFiles(owner, repo, branchOrCommit, relPath));
    }

    @GetMapping("/latest/{owner}/{repo}/{branchOrCommit}")
    @SneakyThrows
    @WrapsException(wrapped = ServiceStatus.NOT_FOUND, status = HttpStatus.NOT_FOUND)
    public RevCommit latestCommitOfCurrentRepo(@PathVariable String owner, @PathVariable String repo,
                                               @PathVariable String branchOrCommit,
                                               @RequestBody(required = false) String relPath) {
        return gitWebService.latestCommitOfCurrentRepo(owner, repo, branchOrCommit, relPath);
    }

    @GetMapping("/file/{owner}/{repo}/{branchOrCommit}")
    @SneakyThrows
    @WrapsException(wrapped = ServiceStatus.NOT_FOUND, status = HttpStatus.NOT_FOUND)
    public Result<byte[]> getFile(@PathVariable String owner,
                                  @PathVariable String repo,
                                  @PathVariable String branchOrCommit,
                                  @RequestParam String filePath) {
        return Result.success(gitWebService.getFile(owner, repo, branchOrCommit, filePath));
    }
}
