package io.zeroparadigm.liquid.auth;

import io.zeroparadigm.liquid.auth.realm.GenericAuthorizationRealm;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;

/**
 * Supports selecting auth method.
 *
 * @author hezean
 */
@Slf4j
public class MultiRealmAuthenticator extends ModularRealmAuthenticator {

    @Override
    protected AuthenticationInfo doAuthenticate(AuthenticationToken authenticationToken)
        throws AuthenticationException {
        assertRealmsConfigured();

        Optional<Realm> realm =
            getRealms().stream()
                .filter(r -> r.getClass() != GenericAuthorizationRealm.class)
                .filter(r -> r.supports(authenticationToken))
                .findFirst();
        if (realm.isPresent()) {
            return doSingleRealmAuthentication(realm.get(), authenticationToken);
        } else {
            return doMultiRealmAuthentication(getRealms(), authenticationToken);
        }
    }
}
