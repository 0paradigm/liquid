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
import io.zeroparadigm.liquid.core.dao.entity.*;
import io.zeroparadigm.liquid.core.dao.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * Fetch and modify milestone info.
 *
 * @author matthewleng
 */
@Api
@Slf4j
@RestController
@RequestMapping("/api/milestone")
public class MileStoneController {

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

    @Autowired
    MileStoneMapper milestoneMapper;

    @GetMapping("/new")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<Boolean> newMileStone(@RequestHeader(value = "Authorization", required = false) String token,
                                        @RequestParam("repoId") Integer repoId,
                                        @RequestParam("name") String name,
                                        @RequestParam("description") String description,
                                        @RequestParam("dueDate") Long dueDate,
                                        @RequestParam("closed") Boolean closed) {
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
        milestoneMapper.createMileStone(repoId, name, description, dueDate, closed);
        return Result.success(true);
    }

    @GetMapping("/find")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<MileStone> findMileStone(@RequestParam("milestoneId") Integer milestoneId) {
        MileStone mileStone = milestoneMapper.findById(milestoneId);
        if (Objects.isNull(mileStone)) {
            return Result.error(ServiceStatus.NOT_FOUND);
        }
        return Result.success(mileStone);
    }

    @GetMapping("/repo")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<List<MileStone>> findMileStoneByRepo(@RequestParam("repoId") Integer repoId) {
        List<MileStone> mileStones = milestoneMapper.findByRepoId(repoId);
        // log.info("milestones: {}", mileStones);
        if (Objects.isNull(mileStones)) {
            return Result.error(ServiceStatus.NOT_FOUND);
        }
        return Result.success(mileStones);
    }

    // FIXME: authorization?
    @GetMapping("/update_due")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<Boolean> updateMileStoneDue(@RequestHeader(value = "Authorization", required = false) String token,
                                              @RequestParam("milestoneId") Integer milestoneId,
                                              @RequestParam("due") Long due) {
        Integer userId = jwtService.getUserId(token);
        User user = userMapper.findById(userId);
        if (Objects.isNull(user)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        MileStone mileStone = milestoneMapper.findById(milestoneId);
        if (Objects.isNull(mileStone)) {
            return Result.error(ServiceStatus.NOT_FOUND);
        }
        Boolean auth = repoMapper.verifyAuth(userId, mileStone.getRepo());
        Repo repo = repoMapper.findById(mileStone.getRepo());
        if (Objects.isNull(repo) || (!auth && !repo.getOwner().equals(userId))) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        milestoneMapper.updateDueById(milestoneId, due);
        return Result.success(true);
    }

    @GetMapping("/delete")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<Boolean> deleteMileStone(@RequestHeader(value = "Authorization", required = false) String token,
                                           @RequestParam("milestoneId") Integer milestoneId) {
        Integer userId = jwtService.getUserId(token);
        User user = userMapper.findById(userId);
        if (Objects.isNull(user)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        MileStone mileStone = milestoneMapper.findById(milestoneId);
        if (Objects.isNull(mileStone)) {
            return Result.error(ServiceStatus.NOT_FOUND);
        }
        Boolean auth = repoMapper.verifyAuth(userId, mileStone.getRepo());
        Repo repo = repoMapper.findById(mileStone.getRepo());
        if (Objects.isNull(repo) || (!auth && !repo.getOwner().equals(userId))) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        milestoneMapper.deleteById(milestoneId);
        return Result.success(true);
    }
}
