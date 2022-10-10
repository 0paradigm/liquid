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

package io.zeroparadigm.liquid.exceptions;

import io.zeroparadigm.liquid.base.constants.ServiceStatus;
import java.text.MessageFormat;
import java.util.Locale;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * When catching spec exceptions, or throw intentionally, can spec the message and error code.
 *
 * @author hezean
 */
@Getter
@Setter
public class ServiceException extends RuntimeException {

    private Integer code;

    public ServiceException(String enMsg, String zhMsg) {
        super(getLocaleMsg(enMsg, zhMsg));
    }

    public ServiceException(String enMsg, String zhMsg, Exception cause) {
        super(getLocaleMsg(enMsg, zhMsg), cause);
    }

    public ServiceException(Integer code, String enMsg, String zhMsg) {
        this(enMsg, zhMsg);
        this.code = code;
    }

    public ServiceException(ServiceStatus status) {
        super(status.getMsg());
        this.code = status.getCode();
    }

    public ServiceException(ServiceStatus status, Object... formatter) {
        super(MessageFormat.format(status.getMsg(), formatter));
        this.code = status.getCode();
    }

    private static String getLocaleMsg(String enMsg, String zhMsg) {
        return Locale.SIMPLIFIED_CHINESE
                .getLanguage()
                .equals(LocaleContextHolder.getLocale().getLanguage())
                        ? zhMsg
                        : enMsg;
    }
}
