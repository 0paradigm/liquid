package io.zeroparadigm.liquid.gateway.realm;

import io.zeroparadigm.liquid.common.bo.UserBO;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * Provides a united @code{@link AuthorizingRealm#doGetAuthorizationInfo(PrincipalCollection)}
 * implementation for all supported realms, since they share the same logic, and Shiro repeats
 * calling it for all realms.
 *
 * @author hezean
 */
@Slf4j
public class GenericAuthorizationRealm extends AuthorizingRealm {

    @Override
    public boolean supports(AuthenticationToken token) {
        return true;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Object principal = principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authInfo = new SimpleAuthorizationInfo();
        if (!(principal instanceof UserBO)) {
            return authInfo;
        }
        UserBO user = (UserBO) Objects.requireNonNull(principal);
        setPermissionsFor(user, authInfo);
        log.info(
            "Authorize succeed for {} via {}, permissions: {}",
            user.getId(),
            principals.getRealmNames(),
            authInfo.getStringPermissions().toString());
        return authInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken)
        throws AuthenticationException {
        return null;
    }

    private void setPermissionsFor(UserBO user, SimpleAuthorizationInfo info) {
        //                List<Role> roleList = roleService.findByUserid(userLogin.getId());
        //                if(CollectionUtils.isNotEmpty(roleList)){
        //                    for(Role role : roleList){
        //                        info.addRole(role.getEnname());
        //
        //                        List<Menu> menuList =
        // menuService.getAllMenuByRoleId(role.getId());
        //                        if(CollectionUtils.isNotEmpty(menuList)){
        //                            for (Menu menu : menuList){
        //                                if(StringUtils.isNoneBlank(menu.getPermission())){
        //
        // info.addStringPermission(menu.getPermission());
        //                                }
        //                            }
        //                        }
        //                    }
        //                }
    }
}