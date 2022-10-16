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

package io.zeroparadigm.liquid.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.zeroparadigm.liquid.dto.LoginCredentials;
import io.zeroparadigm.liquid.exceptions.InvalidCredentialFieldException;
import io.zeroparadigm.liquid.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void testHandleInvalidCredentials() throws Exception {
        LoginCredentials credentials = new LoginCredentials();
        credentials.setType(null);
        assertThatThrownBy(() -> authService.login(credentials))
                .isInstanceOf(InvalidCredentialFieldException.class);
    }
}