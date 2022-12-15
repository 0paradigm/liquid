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
import io.zeroparadigm.liquid.core.dao.entity.Repo;
import io.zeroparadigm.liquid.core.dao.entity.User;
import io.zeroparadigm.liquid.core.dao.mapper.RepoMapper;
import io.zeroparadigm.liquid.core.dao.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;

/**
 * Fetch and modify user info. Register user.
 *
 * @author matthewleng
 */
@Api
@Slf4j
@RestController
@RequestMapping("/api/repo")
public class RepoController {

    @Autowired
    UserDao userDao;

    @DubboReference
    JWTService jwtService;

    @Autowired
    RepoMapper repoMapper;

    @Autowired
    UserMapper userMapper;

    @GetMapping("/find")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<Repo> findRepoByOwnerIdAndName(@RequestHeader("Authorization") String token,
                                                 @RequestParam("name") String name) {
        Integer userId = jwtService.getUserId(token);
        if (Objects.isNull(userId)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        Repo repo = repoMapper.findByOwnerIdAndName(userId, name);
        if (Objects.isNull(repo)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        return Result.success(repo);
    }

    @GetMapping("/search")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<Repo> findRepoByOwnerAndName(@RequestParam("owner") String owner, @RequestParam("name") String name) {
        Repo repo = repoMapper.findByOwnerAndName(owner, name);
        if (Objects.isNull(repo)) {
            return Result.error(ServiceStatus.ACCOUNT_NOT_FOUND);
        }
        return Result.success(repo);
    }

    @GetMapping("count_star")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<Integer> countStar(@RequestParam("repoId") Integer repoId) {
        Integer count = repoMapper.countStarers(repoId);
        if (Objects.isNull(count)) {
            return Result.error(ServiceStatus.ACCOUNT_NOT_FOUND);
        }
        return Result.success(count);
    }

    @GetMapping("count_fork")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<Integer> countFork(@RequestParam("repoId") Integer repoId) {
        Integer count = repoMapper.countForks(repoId);
        if (Objects.isNull(count)) {
            return Result.error(ServiceStatus.ACCOUNT_NOT_FOUND);
        }
        return Result.success(count);
    }

    @GetMapping("count_watch")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<Integer> countWatch(@RequestParam("repoId") Integer repoId) {
        Integer count = repoMapper.countWatchers(repoId);
        if (Objects.isNull(count)) {
            return Result.error(ServiceStatus.ACCOUNT_NOT_FOUND);
        }
        return Result.success(count);
    }

    @GetMapping("/list_starers")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<List<User>> listStarers(@RequestParam("repoId") Integer repoId) {
        List<User> user = repoMapper.listStarers(repoId);
        return Result.success(user);
    }

    @GetMapping("/list_watchers")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<List<User>> listWatchers(@RequestParam("repoId") Integer repoId) {
        List<User> user = repoMapper.listWatchers(repoId);
        return Result.success(user);
    }

    @GetMapping("/list_forks")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<List<Repo>> listForks(@RequestParam("repoId") Integer repoId) {
        List<Repo> repos = repoMapper.listForks(repoId);
        return Result.success(repos);
    }

    @GetMapping("/delete")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<Boolean> deleteRepo(@RequestParam("repoId") Integer repoId) {
        repoMapper.deleteById(repoId);
        return Result.success(true);
    }
}
