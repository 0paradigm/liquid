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

package edu.sustc.liquid.auth;

import edu.sustc.liquid.auth.exceptions.MissingCredentialFieldException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.util.StringUtils;
import org.springframework.lang.Nullable;

/**
 * Pass to Shiro, select auth type from multi realms.
 *
 * @author hezean
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserToken extends UsernamePasswordToken {

    private LoginType type;

    private String login;

    private String phone;
    private String captcha;

    /**
     * Sets whether remember this user for the session.
     *
     * @hidden for test
     */
    public UserToken remember(boolean remember) {
        this.setRememberMe(remember);
        return this;
    }

    /**
     * Uses username or email to log in with general password.
     *
     * @param login username or email
     * @param password website password
     */
    public UserToken password(@Nullable String login, @Nullable String password)
            throws MissingCredentialFieldException {
        if (!StringUtils.hasText(login)) {
            throw new MissingCredentialFieldException("Login field is necessary", "账号为必填项");
        }
        if (!StringUtils.hasText(password)) {
            throw new MissingCredentialFieldException("Password is necessary", "密码不得为空");
        }
        this.type = LoginType.PASSWORD;
        this.setUsername(login);
        this.setPassword(password.toCharArray());
        return this;
    }

    /**
     * Uses sms captcha service to log in.
     *
     * @param phone phone number
     * @param captcha captcha received
     */
    public UserToken phoneCaptcha(@Nullable String phone, @Nullable String captcha)
            throws MissingCredentialFieldException {
        if (!StringUtils.hasText(phone)) {
            throw new MissingCredentialFieldException("Phone number is necessary", "手机号为必填项");
        }
        if (!StringUtils.hasText(captcha)) {
            throw new MissingCredentialFieldException("SMS captcha code is necessary", "验证码不得为空");
        }
        this.type = LoginType.PHONE_CAPTCHA;
        this.phone = phone;
        this.captcha = captcha;
        return this;
    }
}
