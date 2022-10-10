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

package io.zeroparadigm.liquid.service.impl;

import io.zeroparadigm.liquid.auth.ShiroUserLoginToken;
import io.zeroparadigm.liquid.dto.LoginCredentials;
import io.zeroparadigm.liquid.exceptions.InvalidCredentialFieldException;
import io.zeroparadigm.liquid.service.AuthService;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public Subject login(LoginCredentials credentials) throws ShiroException, InvalidCredentialFieldException {
        ShiroUserLoginToken token = handleCredentials(credentials);
        Subject subject = SecurityUtils.getSubject();
        subject.login(token);

        if (subject.isAuthenticated()) {
            return subject;
        } else {
            throw new AuthorizationException();
        }
    }

    private ShiroUserLoginToken handleCredentials(LoginCredentials c) throws InvalidCredentialFieldException {
        ShiroUserLoginToken token = new ShiroUserLoginToken();

        // null enum instance could not call ordinal(): NPE
        if (Objects.isNull(c.getType())) {
            throw new InvalidCredentialFieldException("Invalid login type", "登录方式设置不正确");
        }

        token = switch (c.getType()) {
            case PASSWORD -> token.password(c.getLogin(), c.getPassword());
            case PHONE_CAPTCHA -> token.phoneCaptcha(c.getPhone(), c.getCaptcha());
            default -> null;
        };
        if (Objects.isNull(token)) {
            throw new InvalidCredentialFieldException("Invalid login type", "登录方式设置不正确");
        }
        return token.remember(Boolean.TRUE.equals(c.getRemember()));
    }
}
