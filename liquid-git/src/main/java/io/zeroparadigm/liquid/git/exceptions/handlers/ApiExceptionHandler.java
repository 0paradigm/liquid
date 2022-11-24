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

package io.zeroparadigm.liquid.git.exceptions.handlers;

import io.zeroparadigm.liquid.common.dto.Result;
import io.zeroparadigm.liquid.common.enums.ServiceStatus;
import io.zeroparadigm.liquid.common.exceptions.ServiceException;
import io.zeroparadigm.liquid.common.exceptions.annotations.WrapsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

/**
 * Injects state code and i18n message in rest api response.
 *
 * @author hezean
 */
@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @SuppressWarnings("rawtypes")
    public Result exceptionHandler(ServiceException e) {
        log.error("ServiceException", e);
        return new Result(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @SuppressWarnings({"rawtypes", "checkstyle:MissingJavadocMethod"})
    public ResponseEntity<Result> exceptionHandler(Exception e, HandlerMethod handler) {
        WrapsException we = handler.getMethodAnnotation(WrapsException.class);
        if (we == null) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(
                    Result.error(ServiceStatus.INTERNAL_SERVER_ERROR_ARGS, e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
        ServiceStatus wrapped = we.wrapped();
        log.error(wrapped.getMsg(), e);

        return new ResponseEntity<>(Result.error(wrapped, e), we.status());
    }
}
