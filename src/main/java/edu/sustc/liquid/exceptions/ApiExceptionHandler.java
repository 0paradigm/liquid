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

import edu.sustc.liquid.base.constants.ServiceStatus;
import edu.sustc.liquid.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

/**
 * Injects state code and i18n message in rest api response.
 *
 * @author hezean
 */
@RestControllerAdvice
@ResponseBody
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    @SuppressWarnings("rawtypes")
    public Result exceptionHandler(ServiceException e, HandlerMethod handler) {
        log.error("ServiceException", e);
        return new Result(e.getCode(), e.getMessage());
    }

    @SuppressWarnings({"rawtypes", "checkstyle:MissingJavadocMethod"})
    @ExceptionHandler(Exception.class)
    public Result exceptionHandler(Exception e, HandlerMethod handler) {
        ApiException ae = handler.getMethodAnnotation(ApiException.class);
        if (ae == null) {
            log.error(e.getMessage(), e);
            return Result.error(ServiceStatus.INTERNAL_SERVER_ERROR_ARGS, e.getMessage());
        }
        ServiceStatus stat = ae.value();
        log.error(stat.getMsg(), e);
        return Result.error(stat);
    }
}
