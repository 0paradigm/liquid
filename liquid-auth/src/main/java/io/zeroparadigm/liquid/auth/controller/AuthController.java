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

import cn.javaer.aliyun.sms.SmsClient;
import cn.javaer.aliyun.sms.SmsTemplate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.zeroparadigm.liquid.common.api.core.UserAuthService;
import io.zeroparadigm.liquid.common.bo.UserBO;
import io.zeroparadigm.liquid.common.enums.ServiceStatus;
import io.zeroparadigm.liquid.auth.dto.LoginCredentials;
import io.zeroparadigm.liquid.auth.exception.InvalidCredentialFieldException;
import io.zeroparadigm.liquid.auth.jwt.JwtUtils;
import io.zeroparadigm.liquid.auth.service.AuthService;
import io.zeroparadigm.liquid.auth.dto.Result;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "login module", tags = {"login module"})
@Slf4j
@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private SmsClient smsClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @DubboReference
    UserAuthService userAuthService;

    @ApiOperation(value = "login", notes = "Specify login method and provide credentials")
    @ApiImplicitParam(name = "credentials", value = "login type and related credentials", paramType = "body", required = true, dataTypeClass = LoginCredentials.class)
    @ApiResponses({
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 406, message = "Wrong credentials"),
        @ApiResponse(code = 400, message = "General errors")
    })
    @RequestMapping("/login")
    public Result<Map<String, String>> login(@RequestBody LoginCredentials credentials) {
        Result<Map<String, String>> errResult;
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

    @ApiOperation(value = "captchaLogin", notes = "Login via phone captcha")
    @ApiImplicitParam(name = "credentials", value = "login type and related credentials", paramType = "body", required = true, dataTypeClass = LoginCredentials.class)
    @RequestMapping("login/sms")
    public Result<Map<String, String>> captchaLogin(@RequestBody LoginCredentials credentials) {
        String captcha = credentials.getCaptcha();
        String phone = credentials.getPhone();
        if (captcha == null || phone == null) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        try {
            String redisCode = redisTemplate.opsForValue().get(phone);
            UserBO user = userAuthService.findByPhone(phone);
            if (!StringUtils.isEmpty(redisCode) && captcha.equals(redisCode) && user != null) {
                redisTemplate.delete(phone);
                return Result.success(
                    Map.of(
                        "token",
                        jwtUtils.createTokenFor(
                            user.getId(),
                            credentials.getRemember())));

            } else {
                return Result.error(ServiceStatus.WRONG_CAPTCHA);
            }
        } catch (Exception e) {
            return Result.error(ServiceStatus.ERROR_LOGGING);
        }
    }

    @RequestMapping("/sendcode")
    public Result<String> sendCode(@RequestBody String phone) {
        String redisCode = redisTemplate.opsForValue().get(phone);
        // Can't send the captcha repeatedly within 1 minute
        if (redisCode != null) {
            long l = Long.parseLong(redisCode.substring(7));
            if (System.currentTimeMillis() - l < 60000) {
                return Result.error(ServiceStatus.CAPTCHA_DUPLICATE);
            }
        }
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        try {
            SmsTemplate smsTemplate = SmsTemplate.builder()
                .signName("liquid")
                .templateCode("SMS_264935151")
                .addTemplateParam("code", String.valueOf(code))
                .phoneNumbers(Collections.singletonList(phone))
                .build();
            smsClient.send(smsTemplate);
            code.append("_").append(System.currentTimeMillis());
            //time limit 3 minutes
            redisTemplate.opsForValue().set(phone, String.valueOf(code), 3, TimeUnit.MINUTES);
        } catch (Exception e) {
            return Result.error(ServiceStatus.SENDING_ERROR);
        }
        return Result.success("OK");
    }

    @RequiresAuthentication()
    @GetMapping("/hello/{name}")
    public Result<String> hello(@PathVariable String name) {
        return Result.success(name);
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody String userMail, @RequestBody String userName,
                                   @RequestBody String userPassword) {
        //TODO: need to be implement
        userAuthService.register(userName, userMail, userPassword);
        return null;
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
