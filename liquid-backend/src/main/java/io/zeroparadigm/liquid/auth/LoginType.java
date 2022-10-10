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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.zeroparadigm.liquid.auth.realm.UserPasswordRealm;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.shiro.realm.AuthorizingRealm;

/**
 * Supported login methods.
 *
 * @author hezean
 */
@AllArgsConstructor
@Getter
@SuppressWarnings("AlibabaEnumConstantsMustHaveComment")
public enum LoginType {

    PASSWORD("password", UserPasswordRealm.class),
    GITHUB("github", UserPasswordRealm.class),
    PHONE_CAPTCHA("phone-captcha", UserPasswordRealm.class),
    WECHAT("wechat", UserPasswordRealm.class),
    ;

    private final String identifier;
    private final Class<? extends AuthorizingRealm> realm;

    private static final Map<String, LoginType> NAMES_MAP =
            Arrays.stream(LoginType.values())
                    .collect(Collectors.toMap(lt -> lt.identifier, lt -> lt));

    @JsonCreator
    public static LoginType forValue(String value) {
        return NAMES_MAP.get(value);
    }

    @JsonValue
    public String toValue() {
        return identifier;
    }
}
