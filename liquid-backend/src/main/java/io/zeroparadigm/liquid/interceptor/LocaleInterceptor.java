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

package io.zeroparadigm.liquid.interceptor;

import io.zeroparadigm.liquid.base.constants.Constants;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.WebUtils;

/**
 * Locale checker for requests.
 *
 * <p>Should set the language config as {@code zh_CN} or {@code en_US} in request header
 * 'Liquid-Language'.
 *
 * @author hezean
 */
public class LocaleInterceptor implements HandlerInterceptor {

    @Override
    @SuppressWarnings("java:S3516")
    public boolean preHandle(
                             @NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {
        if (WebUtils.getCookie(request, Constants.LOCALE_INDICATOR_NAME) != null) {
            return true;
        }
        String locale = request.getHeader(Constants.LOCALE_INDICATOR_NAME);
        if (locale != null) {
            LocaleContextHolder.setLocale(StringUtils.parseLocale(locale));
        }
        return true;
    }
}
