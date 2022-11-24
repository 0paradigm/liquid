package io.zeroparadigm.liquid.auth.dto;

import io.zeroparadigm.liquid.auth.enums.LoginType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

/**
 * Login controller dto.
 *
 * @author hezean
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginCredentials {

    private LoginType type;
    private Boolean remember = true;

    @Nullable private String login;
    @Nullable private String password;

    @Nullable private String phone;
    @Nullable private String captcha;

    /**
     * Uses username or email to log in with general password.
     *
     * @param login username or email
     * @param password website password
     * @hidden for test
     */
    public LoginCredentials password(@Nullable String login, @Nullable String password) {
        this.type = LoginType.PASSWORD;
        this.login = login;
        this.setPassword(password);
        return this;
    }

    /**
     * Uses sms captcha service to log in.
     *
     * @param phone phone number
     * @param captcha captcha received
     * @hidden for test
     */
    public LoginCredentials phoneCaptcha(@Nullable String phone, @Nullable String captcha) {
        this.type = LoginType.PHONE_CAPTCHA;
        this.phone = phone;
        this.captcha = captcha;
        return this;
    }
}
