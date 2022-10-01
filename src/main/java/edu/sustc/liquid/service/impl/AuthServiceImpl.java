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

package edu.sustc.liquid.service.impl;

import edu.sustc.liquid.auth.UserToken;
import edu.sustc.liquid.auth.exceptions.MissingCredentialFieldException;
import edu.sustc.liquid.dto.LoginCredentials;
import edu.sustc.liquid.service.AuthService;
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
    public Subject login(LoginCredentials credentials)
            throws ShiroException, MissingCredentialFieldException {
        UserToken token = handleCredentials(credentials);
        Subject subject = SecurityUtils.getSubject();
        subject.login(token);

        if (subject.isAuthenticated()) {
            return subject;
        } else {
            throw new AuthorizationException();
        }
    }

    private UserToken handleCredentials(LoginCredentials c) throws MissingCredentialFieldException {
        UserToken token = new UserToken();
        token =
                switch (c.getType()) {
                    case PASSWORD -> token.password(c.getLogin(), c.getPassword());
                    case PHONE_CAPTCHA -> token.phoneCaptcha(c.getPhone(), c.getCaptcha());
                    default -> token; // TODO
                };
        return token.remember(Boolean.TRUE.equals(c.getRemember()));
    }
}
