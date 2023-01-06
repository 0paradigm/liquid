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

package io.zeroparadigm.liquid.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

/**
 * Login controller dto.
 *
 * @author hezean
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginCredentials {

    private LoginType type;
    private Boolean remember = true;

    @Nullable
    private String login;
    @Nullable
    private String password;

    @Nullable
    private String phone;
    @Nullable
    private String captcha;

    /**
     * Uses username or email to log in with general password.
     *
     * @param login username or email
     * @param password website password
     * @hidden for test
     */
    public LoginCredentials password(@Nullable String login, @Nullable String password) {
        this.type = LoginType.PASSWORD;
        this.login = login;
        this.setPassword(password);
        return this;
    }

    /**
     * Uses sms captcha service to log in.
     *
     * @param phone phone number
     * @param captcha captcha received
     * @hidden for test
     */
    public LoginCredentials phoneCaptcha(@Nullable String phone, @Nullable String captcha) {
        this.type = LoginType.PHONE_CAPTCHA;
        this.phone = phone;
        this.captcha = captcha;
        return this;
    }
}
