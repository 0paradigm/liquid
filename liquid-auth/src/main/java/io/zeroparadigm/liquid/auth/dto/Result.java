package io.zeroparadigm.liquid.auth.dto;

import io.zeroparadigm.liquid.common.enums.ServiceStatus;
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

