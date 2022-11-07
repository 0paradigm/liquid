package io.zeroparadigm.liquid.auth.exception;

import io.zeroparadigm.liquid.auth.ShiroUserLoginToken;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Error parsing @code{@link io.zeroparadigm.liquid.auth.dto.LoginCredentials} dto to @code{@link
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