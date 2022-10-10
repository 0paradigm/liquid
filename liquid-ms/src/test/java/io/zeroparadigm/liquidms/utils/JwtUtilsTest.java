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

package io.zeroparadigm.liquidms.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.zeroparadigm.liquidms.base.constants.Constants;
import java.util.Date;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
class JwtUtilsTest {

    @Autowired
    JwtUtils jwtUtils;

    @Value("${jwt.encrypt-secret}")
    private String secret;

    private String token;

    private static final Integer testUser1__liquid_sa__id = 100001;

    @BeforeEach
    void generateToken() throws Exception {
        Map<String, Object> JWT_HEADERS = Map.of(
                "alg", "HS256",
                "typ", "JWT");

        Algorithm algo = Algorithm.HMAC256(secret);
        token = JWT.create()
                .withHeader(JWT_HEADERS)
                .withClaim(Constants.JWT_USER_ID, testUser1__liquid_sa__id)
                .withIssuedAt(new Date())
                .sign(algo);
    }

    @Test
    void testVerifyToken() throws Exception {
        assertThat(jwtUtils.verify(token)).isTrue();
    }

    @Test
    void testGetUserId() throws Exception {
        assertThat(jwtUtils.getUserId(token)).isEqualTo(testUser1__liquid_sa__id);
    }
}
