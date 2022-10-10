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

package io.zeroparadigm.liquid.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.zeroparadigm.liquid.base.constants.Constants;
import java.util.Date;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Generate and validate JWT token.
 *
 * @author hezean
 */
@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.encrypt-secret}")
    private String secret;

    @Value("${jwt.default-expire-time}")
    private long defaultExpireTime;

    @Value("${jwt.remember-expire-time}")
    private long rememberExpireTime;

    private static final Map<String, Object> JWT_HEADERS =
            Map.of(
                    "alg", "HS256",
                    "typ", "JWT");

    /**
     * Generates token with userId claim.
     *
     * @param userId current subject
     * @param remember controls expire time
     * @return jwt token, null if any exception occurred
     */
    public String createTokenFor(Integer userId, boolean remember) {
        try {
            Date expireDate =
                    new Date(
                            System.currentTimeMillis()
                                    + (remember ? defaultExpireTime : rememberExpireTime));
            Algorithm algo = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withHeader(JWT_HEADERS)
                    .withClaim(Constants.JWT_USER_ID, userId)
                    .withExpiresAt(expireDate)
                    .withIssuedAt(new Date())
                    .sign(algo);
        } catch (Exception e) {
            log.warn("Error creating token for {}", userId, e);
            return "";
        }
    }

    /**
     * Verifies token with secret.
     *
     * @param token jwt token
     * @return if the token is valid
     */
    public boolean verify(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Reads claim field uid.
     *
     * @param token jwt token
     * @return uid in claim
     */
    public Integer getUserId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(Constants.JWT_USER_ID).asInt();
        } catch (JWTDecodeException e) {
            return null;
        }
    }
}
