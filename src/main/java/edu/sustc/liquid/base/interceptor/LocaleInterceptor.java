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

package edu.sustc.liquid.base.interceptor;

import edu.sustc.liquid.base.constants.Constants;
import java.util.Locale;
import java.util.Objects;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
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
@Component
public class LocaleInterceptor implements HandlerInterceptor {

    @Override
    @SuppressWarnings("java:S3516")
    public boolean preHandle(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler) {

        Cookie localeCookie = WebUtils.getCookie(request, Constants.LOCALE_INDICATOR_NAME);
        if (Objects.nonNull(localeCookie)) {
            Locale localeCookie0 = StringUtils.parseLocale(localeCookie.getValue());
            if (Objects.nonNull(localeCookie0)) {
                LocaleContextHolder.setLocale(localeCookie0);
            }
            return true;
        }

        Locale localeHeader =
                StringUtils.parseLocale(request.getHeader(Constants.LOCALE_INDICATOR_NAME));
        if (Objects.nonNull(localeHeader)) {
            LocaleContextHolder.setLocale(localeHeader);
            response.addCookie(
                    new Cookie(Constants.LOCALE_INDICATOR_NAME, localeHeader.getLanguage()));
        }
        return true;
    }
}