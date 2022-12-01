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

package io.zeroparadigm.liquid.auth.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.zeroparadigm.liquid.common.bo.UserBO;
import io.zeroparadigm.liquid.common.enums.ServiceStatus;
import io.zeroparadigm.liquid.auth.dto.LoginCredentials;
import io.zeroparadigm.liquid.auth.exception.InvalidCredentialFieldException;
import io.zeroparadigm.liquid.auth.jwt.JwtUtils;
import io.zeroparadigm.liquid.auth.service.AuthService;
import io.zeroparadigm.liquid.auth.dto.Result;
import java.io.Serializable;
import java.util.Map;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.PostMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "登录模块", tags = {"登录模块"})
@Slf4j
@RestController
public class AuthTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtils jwtUtils;

    @ApiOperation(value = "login", notes = "Specify login method and provide credentials")
    @ApiImplicitParam(name = "credentials", value = "login type and related credentials", paramType = "body", required = true, dataTypeClass = LoginCredentials.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 406, message = "Wrong credentials"),
            @ApiResponse(code = 400, message = "General errors")
    })
    @RequestMapping("/login")
    public Result<Map<String, Serializable>> login(@RequestBody LoginCredentials credentials) {
        Result<Map<String, Serializable>> errResult;
        try {
            Subject subject = authService.login(credentials);
            log.info(
                    "User '{}' logged in via '{}'",
                    credentials.getLogin(),
                    credentials.getType().getIdentifier());
            return Result.success(
                    Map.of(
                            "token",
                            jwtUtils.createTokenFor(
                                    ((UserBO) subject.getPrincipal()).getId(),
                                    credentials.getRemember())));
        } catch (InvalidCredentialFieldException e) {
            errResult = Result.error(ServiceStatus.MISSING_CREDENTIAL, e.getMsg());
        } catch (UnknownAccountException e) {
            errResult = Result.error(ServiceStatus.ACCOUNT_NOT_FOUND);
        } catch (IncorrectCredentialsException e) {
            errResult = Result.error(ServiceStatus.INCORRECT_CREDENTIAL);
        } catch (AuthenticationException e) {
            errResult = Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        return errResult;
    }

    @PostMapping("/whoami")
    public Result<Map<String, Serializable>> whoami() {
        Subject subject = SecurityUtils.getSubject();
        return Result.success(Map.of("name", ((UserBO) subject.getPrincipal()).getId()));
    }

    @RequiresAuthentication()
    @GetMapping("/hello/{name}")
    public Result<String> hello(@PathVariable String name) {
        return Result.success(name);
    }

    @ApiOperation(value = "logout", notes = "Destroy current token in backend")
    @PostMapping("/logout")
    @SuppressWarnings({"rawtypes", "checkstyle:MissingJavadocMethod"})
    public Result logout() {
        SecurityUtils.getSubject().logout();
        log.info(
                "[ip: {}, session: {}] logged out",
                SecurityUtils.getSubject().getSession().getHost(),
                SecurityUtils.getSubject().getSession().getId());
        return Result.success();
    }
}
