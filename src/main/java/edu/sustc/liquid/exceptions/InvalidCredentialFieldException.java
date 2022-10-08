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

package edu.sustc.liquid.exceptions;

import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Error parsing @code{@link edu.sustc.liquid.dto.LoginCredentials} dto to @code{@link
 * edu.sustc.liquid.auth.UserToken}.
 *
 * @author hezean
 */
@Getter
@AllArgsConstructor
public class InvalidCredentialFieldException extends Exception {

    private final String enMsg;
    private final String zhMsg;

    /**
     * Gets i18n error prompt.
     *
     * @return error prompt
     */
    public String getMsg() {
        return Locale.SIMPLIFIED_CHINESE
                        .getLanguage()
                        .equals(LocaleContextHolder.getLocale().getLanguage())
                ? this.zhMsg
                : this.enMsg;
    }
}
