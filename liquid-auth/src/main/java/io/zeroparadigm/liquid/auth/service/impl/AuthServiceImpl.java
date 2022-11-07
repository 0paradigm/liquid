package io.zeroparadigm.liquid.auth.service.impl;

import io.zeroparadigm.liquid.auth.ShiroUserLoginToken;
import io.zeroparadigm.liquid.auth.dto.LoginCredentials;
import io.zeroparadigm.liquid.auth.exception.InvalidCredentialFieldException;
import io.zeroparadigm.liquid.auth.service.AuthService;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {


    @Override
    public Subject login(LoginCredentials credentials)
        throws ShiroException, InvalidCredentialFieldException {
        ShiroUserLoginToken token = handleCredentials(credentials);
        Subject subject = SecurityUtils.getSubject();
        subject.login(token);
        if (subject.isAuthenticated()) {
            return subject;
        } else {
            throw new AuthorizationException();
        }
    }

    private ShiroUserLoginToken handleCredentials(LoginCredentials c)
        throws InvalidCredentialFieldException {
        ShiroUserLoginToken token = new ShiroUserLoginToken();

        // null enum instance could not call ordinal(): NPE
        if (Objects.isNull(c.getType())) {
            throw new InvalidCredentialFieldException("Invalid login type", "登录方式设置不正确");
        }

        token =
            switch (c.getType()) {
                case PASSWORD -> token.password(c.getLogin(), c.getPassword());
                case PHONE_CAPTCHA -> token.phoneCaptcha(c.getPhone(), c.getCaptcha());
                default -> null;
            };
        if (Objects.isNull(token)) {
            throw new InvalidCredentialFieldException("Invalid login type", "登录方式设置不正确");
        }
        return token.remember(Boolean.TRUE.equals(c.getRemember()));
    }
}