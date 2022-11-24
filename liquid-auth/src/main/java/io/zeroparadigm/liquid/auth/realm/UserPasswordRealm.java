package io.zeroparadigm.liquid.auth.realm;

import io.zeroparadigm.liquid.auth.ShiroUserLoginToken;
import io.zeroparadigm.liquid.auth.enums.LoginType;
import io.zeroparadigm.liquid.common.api.core.UserAuthService;
import io.zeroparadigm.liquid.common.bo.UserBO;
import java.util.Objects;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * Liquid account sign in. Username / Email Address + Password
 *
 * @author hezean
 */
public class UserPasswordRealm extends GenericAuthorizationRealm {

    @DubboReference(parameters = {"unicast", "false"})
    UserAuthService authService;

    @Override
    public String getName() {
        return LoginType.PASSWORD.getIdentifier();
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        if (token instanceof ShiroUserLoginToken tok) {
            return tok.getType() == LoginType.PASSWORD;
        } else {
            return false;
        }
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
        throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String login = token.getUsername();
        UserBO user = authService.findByNameOrMail(login);
        if (Objects.isNull(user)) {
            throw new UnknownAccountException();
        }
        return new SimpleAuthenticationInfo(user, authService.getPassword(), getName());
    }

    /** Cleans auth cache after update permissions. */
    public void clearAllCache() {
        getAuthorizationCache().clear();
        getAuthenticationCache().clear();
    }
}
