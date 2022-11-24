package io.zeroparadigm.liquid.auth;

import io.zeroparadigm.liquid.auth.enums.LoginType;
import io.zeroparadigm.liquid.auth.exception.InvalidCredentialFieldException;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.util.StringUtils;
import org.springframework.lang.Nullable;

/**
 * Pass to Shiro, select auth type from multi realms.
 *
 * @author hezean
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ShiroUserLoginToken extends UsernamePasswordToken {

    private LoginType type;

    private String phone;

    private static final String PHONE_NUMBER_PATTERN =
        Pattern.compile("^1[3456789]\\d{9}$").pattern();

    private static final String PHONE_CAPTCHA_PATTERN = Pattern.compile("^\\d{6}$").pattern();

    /**
     * Sets whether remember this user for the session.
     *
     * @hidden for test
     */
    public ShiroUserLoginToken remember(boolean remember) {
        this.setRememberMe(remember);
        return this;
    }

    /**
     * Uses username or email to log in with general password.
     *
     * @param login username or email
     * @param password website password
     */
    public ShiroUserLoginToken password(@Nullable String login, @Nullable String password)
        throws InvalidCredentialFieldException {
        if (!StringUtils.hasText(login)) {
            throw new InvalidCredentialFieldException("Login field is necessary", "账号为必填项");
        }
        if (!StringUtils.hasText(password)) {
            throw new InvalidCredentialFieldException("Password is necessary", "密码不得为空");
        }
        this.type = LoginType.PASSWORD;
        this.setUsername(login);
        this.setPassword(Objects.requireNonNull(password).toCharArray());
        return this;
    }

    /**
     * Uses sms captcha service to log in.
     *
     * @param phone phone number
     * @param captcha captcha received
     */
    public ShiroUserLoginToken phoneCaptcha(@Nullable String phone, @Nullable String captcha)
        throws InvalidCredentialFieldException {
        if (!StringUtils.hasText(phone)) {
            throw new InvalidCredentialFieldException("Phone number is necessary", "手机号为必填项");
        } else if (!Pattern.matches(PHONE_NUMBER_PATTERN, phone)) {
            throw new InvalidCredentialFieldException("Invalid phone number", "手机号不合法");
        }
        if (!StringUtils.hasText(captcha)) {
            throw new InvalidCredentialFieldException("SMS captcha code is necessary", "验证码不得为空");
        } else if (!Pattern.matches(PHONE_CAPTCHA_PATTERN, captcha)) {
            throw new InvalidCredentialFieldException("Invalid SMS captcha code", "验证码不合法");
        }
        this.type = LoginType.PHONE_CAPTCHA;
        this.setUsername(phone);
        this.setPassword(Objects.requireNonNull(captcha).toCharArray());
        return this;
    }
}