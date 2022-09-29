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

import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * Pass to Shiro, select auth type from multi realms.
 *
 * @author hezean
 */
@Getter
@Setter
@NoArgsConstructor
public class UserToken extends UsernamePasswordToken {

    private LoginType loginType;

    private String phone;
    private String captcha;

    /** Sets whether remember this user for the session. */
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
    public UserToken password(String login, String password) {
        this.loginType = LoginType.PASSWORD;
        this.setUsername(login);
        if (Objects.isNull(password)) {
            password = "";
        }
        this.setPassword(password.toCharArray());
        return this;
    }

    /**
     * Uses sms captcha service to log in.
     *
     * @param phone phone number
     * @param captcha captcha received
     */
    public UserToken phoneCaptcha(String phone, String captcha) {
        this.loginType = LoginType.PHONE_CAPTCHA;
        this.phone = phone;
        this.captcha = captcha;
        return this;
    }
}
