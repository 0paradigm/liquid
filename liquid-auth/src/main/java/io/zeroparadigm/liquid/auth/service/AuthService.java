package io.zeroparadigm.liquid.auth.service;

import io.zeroparadigm.liquid.auth.dto.LoginCredentials;
import io.zeroparadigm.liquid.auth.exception.InvalidCredentialFieldException;
import org.apache.shiro.ShiroException;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;

/**
 * Authorization service.
 *
 * @author hezean
 */
@Service
public interface AuthService {

    /**
     * Logs in using shiro.
     *
     * @param credentials includes login method and related credentials
     * @return subject if successfully logged in
     * @throws ShiroException if not logged in
     * @throws InvalidCredentialFieldException if the credential is invalid
     */
    Subject login(LoginCredentials credentials)
        throws ShiroException, InvalidCredentialFieldException;
}
