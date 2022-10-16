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

package io.zeroparadigm.liquid.common.enums;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * When controller throws an exception (or <i>0</i> as <i>success</i>), attach the error code and
 * message to the response.
 *
 * @author hezean
 */
@AllArgsConstructor
@Getter
public enum ServiceStatus {

    // success + http general errors
    SUCCESS(0, "Success", "成功"),
    BAD_REQUEST(400, "Bad Request: {0}", "无效请求：{0}"),
    NOT_FOUND(404, "Page not found", "页面未找到"),
    METHOD_NOT_ALLOWED(405, "Method not allowed", "Web 请求方法不允许"),

    // account errors
    NOT_AUTHENTICATED(1000, "Not authenticated", "账号未认证"),
    ACCOUNT_NOT_FOUND(1001, "Account '{0}' not exists", "账号不存在：{0}"),
    INCORRECT_CREDENTIAL(1002, "Incorrect username or password", "账号密码不匹配"),
    MISSING_CREDENTIAL(1003, "{0}", "{0}"),
    ERROR_LOGGING(1004, "Error logging in, please try again", "服务端异常，请重试"),

    // general errors
    INTERNAL_SERVER_ERROR_ARGS(10000, "Internal server error: {0}", "服务端异常: {0}"),
    REQUEST_PARAMS_NOT_VALID_ERROR(10001, "Request parameter {0} is not valid", "请求参数[{0}]无效"),
    ;

    private final int code;
    private final String enMsg;
    private final String zhMsg;

    /**
     * Gets the prompt message, fit website i18n, fallback as en_US.
     *
     * @return i18n status message
     */
    public String getMsg() {
        return Locale.SIMPLIFIED_CHINESE
                .getLanguage()
                .equals(LocaleContextHolder.getLocale().getLanguage())
                        ? this.zhMsg
                        : this.enMsg;
    }

    /** Finds ServiceStatus entity by status code. */
    public static Optional<ServiceStatus> findStatusBy(int code) {
        return Arrays.stream(ServiceStatus.values()).filter(ss -> ss.code == code).findFirst();
    }
}
