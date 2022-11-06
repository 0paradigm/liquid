package io.zeroparadigm.liquid.gateway.exception;

import io.zeroparadigm.liquid.gateway.ShiroUserLoginToken;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Error parsing @code{@link io.zeroparadigm.liquid.gateway.dto.LoginCredentials} dto to @code{@link
 * ShiroUserLoginToken}.
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