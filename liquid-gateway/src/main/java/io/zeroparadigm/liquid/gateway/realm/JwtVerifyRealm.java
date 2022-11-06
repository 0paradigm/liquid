package io.zeroparadigm.liquid.gateway.realm;

import io.zeroparadigm.liquid.gateway.jwt.JwtToken;
import io.zeroparadigm.liquid.gateway.jwt.JwtUtils;
import io.zeroparadigm.liquid.common.api.core.UserAuthService;
import io.zeroparadigm.liquid.common.bo.UserBO;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Confirms JWT token in header and set the @code{@link UserBO} as principle.
 *
 * @author hezean
 */
@Slf4j
public class JwtVerifyRealm extends GenericAuthorizationRealm {

    @Autowired JwtUtils jwtUtils;

    @DubboReference
    UserAuthService authService;

    @Override
    public String getName() {
        return "jwt-token";
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
        throws AuthenticationException {
        String token = (String) authenticationToken.getCredentials();
        UserBO user = authService.findById(jwtUtils.getUserId(token));
        if (Objects.isNull(user) || !jwtUtils.verify(token)) {
            throw new AuthenticationException("invalid jwt token");
        }
        return new SimpleAuthenticationInfo(user, token, getName());
    }
}