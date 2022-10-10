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

package io.zeroparadigm.liquid.dto;

import io.zeroparadigm.liquid.base.constants.ServiceStatus;
import java.text.MessageFormat;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Provides additional information for controller response.
 *
 * @param <T> response data type
 * @author hezean
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Result<T> {

    private Integer code;
    private String msg;
    private T data;

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ServiceStatus.SUCCESS.getCode(), ServiceStatus.SUCCESS.getMsg(), data);
    }

    @SuppressWarnings("rawtypes")
    public static Result success() {
        return success(null);
    }

    public static <T> Result<T> error(ServiceStatus status, Object... args) {
        return new Result<>(status.getCode(), MessageFormat.format(status.getMsg(), args));
    }

    public boolean is(ServiceStatus status) {
        return Objects.nonNull(this.code) && this.code.equals(status.getCode());
    }
}
