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

import static io.zeroparadigm.liquid.git.service.impl.GitBasicServiceImpl.createMfileByPath;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.zeroparadigm.liquid.common.api.auth.JWTService;
import io.zeroparadigm.liquid.common.api.core.UserAuthService;
import io.zeroparadigm.liquid.common.api.git.GitBasicService;
import io.zeroparadigm.liquid.common.bo.UserBO;
import io.zeroparadigm.liquid.common.dto.Result;
import io.zeroparadigm.liquid.common.enums.ServiceStatus;
import io.zeroparadigm.liquid.common.exceptions.annotations.WrapsException;
import io.zeroparadigm.liquid.git.dto.WebCommitDTO;
import io.zeroparadigm.liquid.git.dto.WebCreateRepoDTO;
import io.zeroparadigm.liquid.git.pojo.LatestCommitInfo;
import io.zeroparadigm.liquid.git.service.GitWebService;

import java.io.IOException;
import java.util.List;

import java.util.Map;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
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

    @DubboReference(parameters = {"unicast", "false"})
    JWTService jwtService;

    @DubboReference(parameters = {"unicast", "false"})
    UserAuthService userAuthService;

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

    @Data
    public static class WebNewDTO {

        String ctx;
        String fileName;
        String path;
        String taskId;
    }

    @PostMapping("/upload2/{owner}/{repo}/{branch}")
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
    public Result uploadText(@PathVariable String owner,
                             @PathVariable String repo,
                             @PathVariable String branch,
                             @RequestBody WebNewDTO dto) {
        MultipartFile mfile = createMfileByPath(dto.getFileName(), dto.getCtx());
        return upload(owner, repo, branch, mfile, dto.getPath(), dto.getTaskId());
    }

    @DeleteMapping("/deletebranch/{owner}/{repo}/{branch}")
    @WrapsException(ServiceStatus.INTERNAL_SERVER_ERROR_ARGS)
    public Result deleteBranch(@PathVariable String owner,
                               @PathVariable String repo,
                               @PathVariable String branch) {
        return gitWebService.branchDelete(owner, repo, branch);
    }

    @PostMapping("/createbranch/{owner}/{repo}/{fromBranch}/{toBranch}")
    @WrapsException(ServiceStatus.INTERNAL_SERVER_ERROR_ARGS)
    public Result createBranch(@PathVariable String owner,
                               @PathVariable String repo,
                               @PathVariable String fromBranch,
                               @PathVariable String toBranch) {
        return gitWebService.branchCheckoutB(owner, repo, fromBranch, toBranch);
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
                               @RequestBody WebCommitDTO args,
                               @RequestHeader(value = "Authorization", required = false) String auth) {
        Integer userId = jwtService.getUserId(auth);
        UserBO userBO = userAuthService.findById(userId);
        log.info("commit with user {}", userBO);
        gitWebService.commit(owner, repo, branch,
                args.getTaskId(), args.getAddFiles(), args.getMessage(), userBO);
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
    public Result<List<LatestCommitInfo>> listFiles(@PathVariable String owner,
                                                    @PathVariable String repo,
                                                    @PathVariable String branchOrCommit,
                                                    @RequestParam(required = false) String relPath) {
        return Result.success(gitWebService.listFiles(owner, repo, branchOrCommit, relPath));
    }

    @GetMapping("/latest/{owner}/{repo}/{branchOrCommit}")
    @SneakyThrows
    @WrapsException(wrapped = ServiceStatus.NOT_FOUND, status = HttpStatus.NOT_FOUND)
    public GitWebService.LatestCommitDTO latestCommitOfCurrentRepo(@PathVariable String owner,
                                                                   @PathVariable String repo,
                                                                   @PathVariable String branchOrCommit,
                                                                   @RequestParam(required = false) String relPath) {
        return gitWebService.latestCommitOfCurrentRepo(owner, repo, branchOrCommit, relPath);
    }

    @Data
    public static class WebDeleteFileDTO {

        String file;
        String msg;
    }

    @DeleteMapping("/deletefile/{owner}/{repo}/{branch}")
    @SneakyThrows
    @WrapsException(wrapped = ServiceStatus.INTERNAL_SERVER_ERROR_ARGS)
    public Result deleteFile(@PathVariable String owner,
                             @PathVariable String repo,
                             @PathVariable String branch,
                             @RequestBody WebDeleteFileDTO args,
                             @RequestHeader(value = "Authorization", required = false) String auth) {
        Integer userId = jwtService.getUserId(auth);
        UserBO userBO = userAuthService.findById(userId);
        log.info("delete file with user {}", userBO);
        return gitWebService.webDelete(owner, repo, branch, args.getFile(), userBO, args.getMsg());
    }

    @GetMapping("/file/{owner}/{repo}/{branchOrCommit}")
    @SneakyThrows
    @WrapsException(wrapped = ServiceStatus.NOT_FOUND, status = HttpStatus.NOT_FOUND)
    public Result<String> getFile(@PathVariable String owner,
                                  @PathVariable String repo,
                                  @PathVariable String branchOrCommit,
                                  @RequestParam String filePath) {
        try {
            String json = gitWebService.getFile(owner, repo, branchOrCommit, filePath);
            return Result.success(json);
        } catch (IOException e) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
    }

    @GetMapping("/{owner}/{repo}/listbranches")
    @SneakyThrows
    @WrapsException(wrapped = ServiceStatus.NOT_FOUND, status = HttpStatus.NOT_FOUND)
    public Result<List<String>> listBranches(@PathVariable String owner,
                                             @PathVariable String repo,
                                             @RequestHeader(value = "Authorization", required = false) String auth) {

        Integer userId = jwtService.getUserId(auth);
        log.info("list branches with user {}", userId);
        if (!userAuthService.hasAccessTo(userId, owner, repo)) {
            throw new IOException();
        }
        return Result.success(gitBasicService.listBranches(owner, repo));
    }

    @GetMapping("/listcommits/{owner}/{repo}/{branchOrCommit}")
    public Result<List<GitWebService.BriefCommitDTO>> listCommits(@PathVariable String owner,
                                                                  @PathVariable String repo,
                                                                  @PathVariable String branchOrCommit) {
        try {
            return Result.success(gitWebService.listCommits(owner, repo, branchOrCommit));
        } catch (Exception e) {
            log.error("list commits error", e);
            return Result.success(List.of());
        }
    }

    @GetMapping("/revert/{owner}/{repo}")
    public Result revert(@PathVariable String owner,
                         @PathVariable String repo,
                         @RequestParam String branch,
                         @RequestParam String sha,
                         @RequestHeader(value = "Authorization", required = false) String auth) {
        Integer userId = jwtService.getUserId(auth);
        UserBO userBO = userAuthService.findById(userId);
        gitWebService.webRevert(owner, repo, branch, sha, userBO);
        return Result.success();
    }

    @GetMapping("/diff/{owner}/{repo}")
    @ApiOperation(value = "diff", notes = "Return is JSON String")
    public Result<List<Map<String, Object>>> diff(@PathVariable String owner,
                                                  @PathVariable String repo,
                                                  @RequestParam String branch,
                                                  @RequestParam String sha) {
        try {
            List<Map<String, Object>> res = gitWebService.changesOfCommit(owner, repo, branch, sha);
            return Result.success(res);
        } catch (Exception e) {
            log.error("Diff error", e);
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
    }

    @GetMapping("/diffV2/{owner}/{repo}")
    @ApiOperation(value = "diff2", notes = "Return is JSON String")
    public Result<List<Map<String, String>>> diffV2(@PathVariable String owner,
                                                    @PathVariable String repo,
                                                    @RequestParam String branch,
                                                    @RequestParam String sha) {
        try {
            List<Map<String, String>> res =
                    gitWebService.changesOfCommitV2(owner, repo, branch, sha);
            return Result.success(res);
        } catch (Exception e) {
            log.error("Diff error", e);
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
    }

    @ResponseBody
    @PostMapping("/internal/v1/sync/{owner}/{repo}")
    public void updateCaches(@PathVariable String owner, @PathVariable String repo) {
        gitWebService.updateCaches(owner, repo);
    }

    // @GetMapping("/commitdiff/{owner}/{repo}/{sha}")
    // public Result commitDiff(@PathVariable String owner,
    // @PathVariable String repo,
    // @PathVariable String sha) {
    // return Result.success(gitWebService.listFilesChangesOfCommit(owner, repo, sha));
    // }

    @GetMapping("/getPRCommit")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<List<GitWebService.BriefCommitDTO>> getPRCommit(
                                                                  @RequestParam("head_owner") String headOwner,
                                                                  @RequestParam("head_repo") String headRepo,
                                                                  @RequestParam("head_branch") String headBranch,
                                                                  @RequestParam("base_owner") String baseOwner,
                                                                  @RequestParam("base_repo") String baseRepo,
                                                                  @RequestParam("base_branch") String baseBranch) {
        try {
            List<GitWebService.BriefCommitDTO> list =
                    gitWebService.listPRCommit(headOwner, headRepo, headBranch, baseOwner, baseRepo,
                            baseBranch);
            return Result.success(list);
        } catch (Exception e) {
            log.error("List commits error", e);
            return Result.error(ServiceStatus.METHOD_NOT_ALLOWED);
        }
    }

    @GetMapping("/getPRDiff")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result getPRDiff(@RequestParam("head_owner") String headOwner,
                            @RequestParam("head_repo") String headRepo,
                            @RequestParam("head_branch") String headBranch,
                            @RequestParam("base_owner") String baseOwner,
                            @RequestParam("base_repo") String baseRepo,
                            @RequestParam("base_branch") String baseBranch,
                            @RequestParam(value = "recursive") Boolean recursive) {
        try {
            var noRecur =
                    gitWebService.diffPR(headOwner, headRepo, headBranch, baseOwner, baseRepo,
                            baseBranch, false);
            var recur = gitWebService.handleDiff((List<Map<String, String>>) noRecur);
            return Result.success(Map.of("noRecur", noRecur, "recur", recur));
        } catch (Exception e) {
            log.error("List commits error", e);
            return Result.error(ServiceStatus.METHOD_NOT_ALLOWED);
        }
    }

    @GetMapping("/mergePR")
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result mergePR(@RequestParam("head_owner") String headOwner,
                          @RequestParam("head_repo") String headRepo,
                          @RequestParam("head_branch") String headBranch,
                          @RequestParam("base_owner") String baseOwner,
                          @RequestParam("base_repo") String baseRepo,
                          @RequestParam("base_branch") String baseBranch,
                          @RequestParam("pr_title") String prTitle) {
        try {
            gitWebService.mergePR(baseOwner, baseRepo, baseBranch, headOwner, headRepo, headBranch,
                    prTitle);
            return Result.success();
        } catch (Exception e) {
            log.error("List commits error", e);
            return Result.error(ServiceStatus.METHOD_NOT_ALLOWED);
        }
    }
}
