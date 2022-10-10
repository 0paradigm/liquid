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

package io.zeroparadigm.liquid.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.zeroparadigm.liquid.exceptions.InvalidCredentialFieldException;
import org.junit.jupiter.api.Test;

class UserTokenTest {

    @Test
    void testPasswordToken() throws Exception {
        assertThat(new ShiroUserLoginToken().password("user", "pwd").remember(true)).isNotNull();
        assertThatThrownBy(() -> new ShiroUserLoginToken().password("user", ""))
                .isInstanceOf(InvalidCredentialFieldException.class);
        assertThatThrownBy(() -> new ShiroUserLoginToken().password("user", null))
                .isInstanceOf(InvalidCredentialFieldException.class);
        assertThatThrownBy(() -> new ShiroUserLoginToken().password(null, ""))
                .isInstanceOf(InvalidCredentialFieldException.class);
    }

    @Test
    void testPhoneCaptchaToken() throws Exception {
        assertThat(new ShiroUserLoginToken().phoneCaptcha("17600001234", "123456")).isNotNull();
        assertThatThrownBy(() -> new ShiroUserLoginToken().phoneCaptcha("10000000000000", "a21d"))
                .isInstanceOf(InvalidCredentialFieldException.class);
        assertThatThrownBy(() -> new ShiroUserLoginToken().phoneCaptcha("17600001234", "12a"))
                .isInstanceOf(InvalidCredentialFieldException.class);
        assertThatThrownBy(() -> new ShiroUserLoginToken().phoneCaptcha("10000000000000", "123456"))
                .isInstanceOf(InvalidCredentialFieldException.class);
    }

    @Test
    void testRememberToken() throws Exception {
        assertThat(new ShiroUserLoginToken().remember(true).isRememberMe()).isTrue();
    }
}