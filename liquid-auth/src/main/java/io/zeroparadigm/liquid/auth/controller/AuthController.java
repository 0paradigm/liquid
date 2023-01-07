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

import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.zeroparadigm.liquid.auth.dto.LoginCredentials;
import io.zeroparadigm.liquid.auth.dto.Result;
import io.zeroparadigm.liquid.auth.exception.InvalidCredentialFieldException;
import io.zeroparadigm.liquid.auth.jwt.JwtUtils;
import io.zeroparadigm.liquid.auth.service.AuthService;
import io.zeroparadigm.liquid.common.api.core.UserAuthService;
import io.zeroparadigm.liquid.common.bo.UserBO;
import io.zeroparadigm.liquid.common.enums.ServiceStatus;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "login module", tags = {"login module"})
@Slf4j
@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtils jwtUtils;

    ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, String> captchas = new HashMap<>();

    Random random = new Random();

    @DubboReference(parameters = {"unicast", "false"})
    UserAuthService userAuthService;

    @ApiOperation(value = "login", notes = "Specify login method and provide credentials")
    @ApiImplicitParam(name = "credentials", value = "login type and related credentials", paramType = "body", required = true, dataTypeClass = LoginCredentials.class)
    @PostMapping("/login")
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
    @PostMapping("/sms")
    public Result<Map<String, String>> captchaLogin(@RequestBody LoginCredentials credentials) {
        String captcha = credentials.getCaptcha();
        String phone = credentials.getPhone();
        if (captcha == null || phone == null) {
            return Result.error(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR);
        }
        try {
            String redisCode = captchas.get(phone);
            UserBO user = userAuthService.findByPhone(phone);
            if (captcha.equals(redisCode) && user.getId() != null) {
                captchas.remove(phone);
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

    @ApiOperation(value = "captcha", notes = "Send captcha to user")
    @ApiImplicitParam(name = "phone", value = "user's phone number")
    @PostMapping("/captcha")
    public Result<String> sendCode(@RequestParam(value = "phone") String phone) {
        if (userAuthService.findByPhone(phone).getId() == null) {
            return Result.error(ServiceStatus.PHONE_NOT_REGISTERED);
        }
        StringBuilder ss = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            ss.append(random.nextInt(10));
        }
        try {
            com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                    .setAccessKeyId("LTAI5tDCCZgDT7ounNJN1exq")
                    .setAccessKeySecret("IBypcl8baB7201PI4V6zpK5wgRyeJz");
            config.endpoint = "dysmsapi.aliyuncs.com";
            com.aliyun.dysmsapi20170525.Client client =
                    new com.aliyun.dysmsapi20170525.Client(config);
            com.aliyun.dysmsapi20170525.models.SendSmsRequest sendSmsRequest =
                    new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
                            .setSignName("liquid")
                            .setTemplateCode("SMS_264870471")
                            .setPhoneNumbers(phone)
                            .setTemplateParam(String.format("{\"code\":\"%s\"}", ss));
            com.aliyun.teautil.models.RuntimeOptions runtime =
                    new com.aliyun.teautil.models.RuntimeOptions();
            SendSmsResponse sendSmsResponse = client.sendSmsWithOptions(sendSmsRequest, runtime);
            captchas.put(phone, ss.toString());
            log.info("sent {} to {}, resp {}", ss.toString(), phone, sendSmsResponse);
            log.info("all captchas: {}", captchas);
            return Result.success(objectMapper.writeValueAsString(sendSmsResponse));
        } catch (Exception error) {
            log.info(error.getMessage());
            return Result.error(ServiceStatus.SENDING_ERROR);
        }
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
