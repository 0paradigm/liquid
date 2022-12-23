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
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.zeroparadigm.liquid.common.api.auth.JWTService;
import io.zeroparadigm.liquid.common.dto.Result;
import io.zeroparadigm.liquid.common.enums.ServiceStatus;
import io.zeroparadigm.liquid.common.exceptions.annotations.WrapsException;
import io.zeroparadigm.liquid.core.dao.UserDao;
import io.zeroparadigm.liquid.core.dao.entity.User;
import io.zeroparadigm.liquid.core.dao.mapper.RepoMapper;
import io.zeroparadigm.liquid.core.dao.mapper.UserMapper;
import io.zeroparadigm.liquid.core.dto.RepoDto;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import io.zeroparadigm.liquid.core.dao.entity.Repo;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * Fetch and modify user info. Register user.
 *
 * @author hezean
 */
@Api
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserDao userDao;

    @DubboReference(parameters = {"unicast", "false"})
    JWTService jwtService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    RepoMapper repoMapper;

    @PostMapping("/create")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<Boolean> createUser(@RequestParam("login") String login, @RequestParam("name") String name, @RequestParam("email") String email,
                                      @Nullable @RequestParam("twitter_username") String twitter_username, @Nullable @RequestParam("bio") String bio,
                                      @Nullable @RequestParam("company") String company, @Nullable @RequestParam("location") String location,
                                      @RequestParam("password") String password, @Nullable @RequestParam("phone") String phone){
        User user = new User();
        user.setLogin(login);
        user.setName(name);
        user.setEmail(email);
        user.setTwitterUsername(twitter_username);
        user.setBio(bio);
        user.setCompany(company);
        user.setLocation(location);
        user.setPassword(password);
        user.setPhone(phone);
        user.setCreatedAt(System.currentTimeMillis());
        user.setUpdatedAt(System.currentTimeMillis());
        userMapper.insert(user);
        return Result.success();
    }

    @PostMapping("/update")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<Boolean> updateUser(@RequestHeader("Authorization") String token,
                                      @Nullable @RequestParam("twitter_username") String twitter_username,
                                      @Nullable @RequestParam("bio") String bio,
                                      @Nullable @RequestParam("company") String company,
                                      @Nullable @RequestParam("location") String location,
                                       @Nullable @RequestParam("phone") String phone){
        Integer userId = jwtService.getUserId(token);
        User user = userMapper.selectById(userId);
        if (Objects.isNull(user)) {
            return Result.error(ServiceStatus.ACCOUNT_NOT_FOUND);
        }
        userMapper.updateUserById(userId, twitter_username, bio, company, location, phone, System.currentTimeMillis());
        return Result.success();
    }

    @GetMapping("/info")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<User> getUserInfo(@RequestHeader("Authorization") String token){
        Integer userId = jwtService.getUserId(token);
        User user = userMapper.selectById(userId);
        if (Objects.isNull(user)) {
            return Result.error(ServiceStatus.ACCOUNT_NOT_FOUND);
        }
        return Result.success(user);
    }

    @GetMapping("/find")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<User> findUserByNameOrMail(@RequestParam("usr") String name_or_mail) {
        User user = userMapper.findByNameOrMail(name_or_mail);
        if (Objects.isNull(user)) {
            return Result.error(ServiceStatus.ACCOUNT_NOT_FOUND);
        }
        return Result.success(user);
    }

    @GetMapping("/search")
    @WrapsException(ServiceStatus.ACCOUNT_NOT_FOUND)
    public Result<List<User>> fuzzySearch(@RequestParam("usr") String name_or_mail) {
        List<User> users = userMapper.fuzzySearch(name_or_mail);
        if (Objects.isNull(users)) {
            return Result.error(ServiceStatus.ACCOUNT_NOT_FOUND);
        }
        return Result.success(users);
    }

    @GetMapping("/star")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<Boolean> star(@RequestHeader("Authorization") String token, @RequestParam("id") Integer id) {
        Integer userId = jwtService.getUserId(token);
        if (Objects.isNull(userId)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        User usr = userMapper.findById(userId);
        userMapper.starRepo(usr.getLogin(), id);
        return Result.success();
    }

    @GetMapping("/unstar")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<Boolean> unstar(@RequestHeader("Authorization") String token, @RequestParam("id") Integer id) {
        Integer userId = jwtService.getUserId(token);
        if (Objects.isNull(userId)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        User usr = userMapper.findById(userId);
//        log.info("user/unstar param  " + usr.getLogin() + " " + id);
        userMapper.unstarRepo(usr.getLogin(), id);
        return Result.success();
    }

    @GetMapping("/watch")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<Boolean> watch(@RequestHeader("Authorization") String token, @RequestParam("id") Integer id,
                                 @RequestParam("particip") Boolean participation, @RequestParam("issue") Boolean issue,
                                 @RequestParam("pull") Boolean pull, @RequestParam("release") Boolean release,
                                 @RequestParam("discuss") Boolean discussion, @RequestParam("alerts") Boolean security_alerts
    ) {
        Integer userId = jwtService.getUserId(token);
        if (Objects.isNull(userId)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        User usr = userMapper.findById(userId);
        userMapper.watchRepo(usr.getLogin(), id, participation, issue, pull, release, discussion, security_alerts);
        return Result.success();
    }

    @GetMapping("/unwatch")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<Boolean> unwatch(@RequestHeader("Authorization") String token, @RequestParam("id") Integer id) {
        Integer userId = jwtService.getUserId(token);
        if (Objects.isNull(userId)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        User usr = userMapper.findById(userId);
        userMapper.unwatchRepo(usr.getLogin(), id);
        return Result.success();
    }

    // forked from
    @GetMapping("/repo")
    @WrapsException(ServiceStatus.NOT_AUTHENTICATED)
    public Result<List<RepoDto>> getRepo(@RequestHeader("Authorization") String token) {
        Integer userId = jwtService.getUserId(token);
        if (Objects.isNull(userId)) {
            return Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        User usr = userMapper.findById(userId);
        List<Repo> repos = userMapper.listUserRepos(usr.getLogin());
        List<RepoDto> repoDtos = new ArrayList<>();
        for (int i = 0; i < repos.size(); i++) {
            String forkedFrom = null;
            if (repos.get(i).getForkedFrom() != null) {
                Repo repo = repoMapper.findById(repos.get(i).getForkedFrom());
                if (!Objects.isNull(repo)){
                    User owner = userMapper.findById(repo.getOwner());
                    forkedFrom = owner.getLogin() + "/" + repo.getName();
                }
            }
            log.info("user/repo: repo is " + repos.get(i));
            Integer star = repoMapper.countStarers(repos.get(i).getId());
            Integer fork = repoMapper.countForks(repos.get(i).getId());
            Integer watch = repoMapper.countWatchers(repos.get(i).getId());
            repoDtos.add(new RepoDto(repos.get(i).getId(), usr.getLogin(), repos.get(i).getName(),
                repos.get(i).getDescription(), repos.get(i).getLanguage(),forkedFrom, repos.get(i).getPrivated(),
                star, fork, watch));
        }
        return Result.success(repoDtos);
    }

    /**
     * Says hello to the person.
     *
     * @param name person name
     * @return a greeting message
     */
    @ApiOperation(value = "hello", notes = "says hello to the person")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "USER_NAME", required = true, dataTypeClass = String.class, example = "chris")
    })
    @PostMapping(value = "/hello")
    @ResponseStatus(HttpStatus.CREATED)
    @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<String> hello(
            @RequestParam String name,
            @RequestBody(required = false) String ts) {
        log.debug("User {}", name);
        return Result.success("userService.greet(name)");
    }

    /**
     * Queries the first user with that name.
     *
     * @param name person name
     * @return first user
     */
    @ApiOperation(value = "getUserNamedAs", notes = "queries the first user with that name")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "user's name", required = true, dataTypeClass = String.class, example = "foo")
    })
    @PostMapping(value = "/user")
    @ResponseStatus(HttpStatus.OK)
    @WrapsException(value = ServiceStatus.INTERNAL_SERVER_ERROR_ARGS, status = HttpStatus.NOT_ACCEPTABLE)
    public Result<User> getUserNamedAs(@RequestParam(value = "name") String name) {
        throw new RuntimeException();
    }


    @PostMapping(value = "/userId")
    public Result<Integer> getUserIdViaJWT(@RequestHeader(value = "Token") String jwt){
        log.info("token-->"+jwt);
        Result<Integer> result;
        Integer id = jwtService.getUserId(jwt);
        if (id!=null){
            return Result.success(id);
        } else {
            return Result.error(ServiceStatus.ERROR_LOGGING, -1);
        }
    }
}
