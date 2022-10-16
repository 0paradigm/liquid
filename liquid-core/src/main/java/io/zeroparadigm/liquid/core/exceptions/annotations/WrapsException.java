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

package io.zeroparadigm.liquid.core.exceptions.annotations;

import io.zeroparadigm.liquid.common.enums.ServiceStatus;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.http.HttpStatus;

/**
 * Exceptions may be thrown by controllers, wraps real Java exceptions.
 *
 * @author hezean
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WrapsException {

    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    @AliasFor("wrapped")
    ServiceStatus value() default ServiceStatus.INTERNAL_SERVER_ERROR_ARGS;

    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    @AliasFor("value")
    ServiceStatus wrapped() default ServiceStatus.INTERNAL_SERVER_ERROR_ARGS;

    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    HttpStatus status() default HttpStatus.BAD_REQUEST;
}
