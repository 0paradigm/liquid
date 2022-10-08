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

import edu.sustc.liquid.exceptions.InvalidCredentialFieldException;
import java.util.Objects;
import java.util.regex.Pattern;
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

    private String phone;

    private static final String PHONE_NUMBER_PATTERN =
            Pattern.compile("^1[3456789]\\d{9}$").pattern();
    private static final String PHONE_CAPTCHA_PATTERN = Pattern.compile("^\\d{6}$").pattern();

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
            throws InvalidCredentialFieldException {
        if (!StringUtils.hasText(login)) {
            throw new InvalidCredentialFieldException("Login field is necessary", "账号为必填项");
        }
        if (!StringUtils.hasText(password)) {
            throw new InvalidCredentialFieldException("Password is necessary", "密码不得为空");
        }
        this.type = LoginType.PASSWORD;
        this.setUsername(login);
        this.setPassword(Objects.requireNonNull(password).toCharArray());
        return this;
    }

    /**
     * Uses sms captcha service to log in.
     *
     * @param phone phone number
     * @param captcha captcha received
     */
    public UserToken phoneCaptcha(@Nullable String phone, @Nullable String captcha)
            throws InvalidCredentialFieldException {
        if (!StringUtils.hasText(phone)) {
            throw new InvalidCredentialFieldException("Phone number is necessary", "手机号为必填项");
        } else if (!Pattern.matches(PHONE_NUMBER_PATTERN, phone)) {
            throw new InvalidCredentialFieldException("Invalid phone number", "手机号不合法");
        }
        if (!StringUtils.hasText(captcha)) {
            throw new InvalidCredentialFieldException("SMS captcha code is necessary", "验证码不得为空");
        } else if (!Pattern.matches(PHONE_CAPTCHA_PATTERN, captcha)) {
            throw new InvalidCredentialFieldException("Invalid SMS captcha code", "验证码不合法");
        }
        this.type = LoginType.PHONE_CAPTCHA;
        this.setUsername(phone);
        this.setPassword(Objects.requireNonNull(captcha).toCharArray());
        return this;
    }
}
