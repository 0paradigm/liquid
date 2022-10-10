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

package io.zeroparadigm.liquid.controller;

import io.zeroparadigm.liquid.base.constants.ServiceStatus;
import io.zeroparadigm.liquid.dao.UserDao;
import io.zeroparadigm.liquid.dao.entity.User;
import io.zeroparadigm.liquid.dto.Result;
import io.zeroparadigm.liquid.exceptions.annotations.WrapsException;
import io.zeroparadigm.liquid.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

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
    @Autowired
    UserService userService;

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
    // @WrapsException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<String> hello(
                                @RequestParam(value = "name") String name,
                                @ApiIgnore @RequestBody(required = false) String ts) {
        log.debug("User {}", name);
        return Result.success(userService.greet(name));
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
}
