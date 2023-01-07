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

package io.zeroparadigm.liquid.core.controller;

import io.swagger.annotations.Api;
import io.zeroparadigm.liquid.common.api.auth.JWTService;
import io.zeroparadigm.liquid.common.dto.Result;
import io.zeroparadigm.liquid.common.enums.ServiceStatus;
import io.zeroparadigm.liquid.common.exceptions.annotations.WrapsException;
import io.zeroparadigm.liquid.core.dao.UserDao;
import io.zeroparadigm.liquid.core.dao.entity.IssueLabel;
import io.zeroparadigm.liquid.core.dao.entity.Repo;
import io.zeroparadigm.liquid.core.dao.entity.User;
import io.zeroparadigm.liquid.core.dao.mapper.IssueLabelMapper;
import io.zeroparadigm.liquid.core.dao.mapper.IssueMapper;
import io.zeroparadigm.liquid.core.dao.mapper.RepoMapper;
import io.zeroparadigm.liquid.core.dao.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * Fetch and modify issue label info.
 *
 * @author matthewleng
 */
@Api
@Slf4j
@RestController
@RequestMapping("/api/issuelabel")
public class IssueLabelController {

    @Autowired
    UserDao userDao;

    @DubboReference(parameters = {"unicast", "false"})
    JWTService jwtService;

    @Autowired
    RepoMapper repoMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    IssueMapper issueMapper;

    @Autowired
    IssueLabelMapper issueLabelMapper;

    @GetMapping("/new")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<Boolean> newIssueLabel(@RequestHeader(value = "Authorization", required = false) String token,
                                         @RequestParam("repoId") Integer repoId,
                                         @RequestParam("name") String labelName,
                                         @RequestParam("color") String labelColor,
                                         @RequestParam("description") String labelDescription) {
        Integer userId = jwtService.getUserId(token);
        User user = userMapper.findById(userId);
        if (Objects.isNull(user)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        Boolean auth = repoMapper.verifyAuth(userId, repoId);
        Repo repo = repoMapper.findById(repoId);
        if (Objects.isNull(repo) || (!auth && !repo.getOwner().equals(userId))) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        issueLabelMapper.createIssueLabel(repoId, labelName, labelColor, labelDescription);
        return Result.success(true);
    }

    @GetMapping("/find")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<IssueLabel> findIssueLabelById(@RequestParam("id") Integer id) {
        IssueLabel issueLabel = issueLabelMapper.findById(id);
        if (Objects.isNull(issueLabel)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        return Result.success(issueLabel);
    }

    // FIXME: authorization?
    @GetMapping("/delete")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<Boolean> deleteIssueLabelById(@RequestHeader(value = "Authorization", required = false) String token,
                                                @RequestParam("id") Integer id) {
        Integer userId = jwtService.getUserId(token);
        User user = userMapper.findById(userId);
        if (Objects.isNull(user)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        IssueLabel issueLabel = issueLabelMapper.findById(id);
        if (Objects.isNull(issueLabel)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        Boolean auth = repoMapper.verifyAuth(userId, issueLabel.getRepo());
        Repo repo = repoMapper.findById(issueLabel.getRepo());
        if (Objects.isNull(repo) || (!auth && !repo.getOwner().equals(userId))) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        issueLabelMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/repo")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<List<IssueLabel>> findIssueLabelByRepoId(@RequestParam("repoId") Integer repoId) {
        List<IssueLabel> issueLabels = issueLabelMapper.findByRepoId(repoId);
        if (Objects.isNull(issueLabels)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        return Result.success(issueLabels);
    }

    // FIXME: authorization?
    @GetMapping("/delete_repo_name")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<Boolean> deleteIssueLabelByRepoAndName(@RequestHeader(value = "Authorization", required = false) String token,
                                                         @RequestParam("repoId") Integer repoId,
                                                         @RequestParam("name") String name) {
        Integer userId = jwtService.getUserId(token);
        User user = userMapper.findById(userId);
        if (Objects.isNull(user)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        Repo repo = repoMapper.findById(repoId);
        if (Objects.isNull(repo)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        Boolean auth = repoMapper.verifyAuth(userId, repoId);
        if (Objects.isNull(repo) || (!auth && !repo.getOwner().equals(userId))) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        issueLabelMapper.deleteIssueLabel(repoId, name);
        return Result.success();
    }

    @GetMapping("/find_repo_name")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<IssueLabel> findIssueLabelByRepoAndName(@RequestParam("repoId") Integer repoId,
                                                          @RequestParam("name") String name) {
        IssueLabel issueLabel = issueLabelMapper.findByRepoIdAndName(repoId, name);
        log.info("issueLabel: {}", issueLabel);
        if (Objects.isNull(issueLabel)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        return Result.success(issueLabel);
    }
}
