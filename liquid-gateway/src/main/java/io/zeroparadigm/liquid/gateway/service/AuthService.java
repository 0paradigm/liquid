package io.zeroparadigm.liquid.gateway.service;

import io.zeroparadigm.liquid.gateway.dto.LoginCredentials;
import io.zeroparadigm.liquid.gateway.exception.InvalidCredentialFieldException;
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
