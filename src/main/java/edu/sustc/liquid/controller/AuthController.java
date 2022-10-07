/*******************************************************************************
 *    $$\      $$\                     $$\       $$\
 *    $$ |     \__|                    \__|      $$ |
 *    $$ |     $$\  $$$$$$\  $$\   $$\ $$\  $$$$$$$ |
 *    $$ |     $$ |$$  __$$\ $$ |  $$ |$$ |$$  __$$ |
 *    $$$$$$$$\$$ |\$$$$$$$ |\$$$$$$  |$$ |\$$$$$$$ |
 *    \________\__| \____$$ | \______/ \__| \_______|
 *                       $$ |
 *                       \__|  :: Liquid ::  (c) 2022
 *
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
 *******************************************************************************/

package edu.sustc.liquid.controller;

import edu.sustc.liquid.base.constants.ServiceStatus;
import edu.sustc.liquid.dto.LoginCredentials;
import edu.sustc.liquid.dto.Result;
import edu.sustc.liquid.exceptions.InvalidCredentialFieldException;
import edu.sustc.liquid.exceptions.annotations.WrapsException;
import edu.sustc.liquid.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.Serializable;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User login, logout, permission management.
 *
 * @author hezean
 */
@Api(value = "account related services: login / logout / modify permission...")
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired AuthService authService;

    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    @ApiOperation(value = "login", notes = "Specify login method and provide credentials")
    @ApiImplicitParam(
            name = "credentials",
            value = "login type and related credentials",
            paramType = "body",
            required = true,
            dataTypeClass = LoginCredentials.class)
    @ApiResponses({
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 406, message = "Wrong credentials"),
        @ApiResponse(code = 400, message = "General errors")
    })
    @WrapsException(ServiceStatus.ERROR_LOGGING)
    @PostMapping("/login")
    public ResponseEntity<Result<Map<String, Serializable>>> login(
            @RequestBody LoginCredentials credentials) {
        Result<Map<String, Serializable>> errResult;
        try {
            Subject subject = authService.login(credentials);
            log.info(
                    "User '{}' logged in via '{}'",
                    credentials.getLogin(),
                    credentials.getType().getIdentifier());
            return new ResponseEntity<>(
                    Result.success(Map.of("token", subject.getSession().getId())), HttpStatus.OK);
        } catch (InvalidCredentialFieldException e) {
            errResult = Result.error(ServiceStatus.MISSING_CREDENTIAL, e.getMsg());
        } catch (UnknownAccountException e) {
            errResult = Result.error(ServiceStatus.ACCOUNT_NOT_FOUND);
        } catch (IncorrectCredentialsException e) {
            errResult = Result.error(ServiceStatus.INCORRECT_CREDENTIAL);
        } catch (AuthenticationException e) {
            errResult = Result.error(ServiceStatus.NOT_AUTHENTICATED);
        }
        return new ResponseEntity<>(errResult, HttpStatus.NOT_ACCEPTABLE);
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
