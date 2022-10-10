/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.zeroparadigm.liquid.auth.realm;

import io.zeroparadigm.liquid.dao.entity.User;
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
        if (!(principal instanceof User)) {
            return authInfo;
        }
        User user = (User) Objects.requireNonNull(principal);
        setPermissionsFor(user, authInfo);
        log.info(
                "Authorize succeed for {} via {}, permissions: {}",
                user.getId(),
                principals.getRealmNames(),
                authInfo.getStringPermissions().toString());
        return authInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        return null;
    }

    private void setPermissionsFor(User user, SimpleAuthorizationInfo info) {
        // List<Role> roleList = roleService.findByUserid(userLogin.getId());
        // if(CollectionUtils.isNotEmpty(roleList)){
        // for(Role role : roleList){
        // info.addRole(role.getEnname());
        //
        // List<Menu> menuList =
        // menuService.getAllMenuByRoleId(role.getId());
        // if(CollectionUtils.isNotEmpty(menuList)){
        // for (Menu menu : menuList){
        // if(StringUtils.isNoneBlank(menu.getPermission())){
        //
        // info.addStringPermission(menu.getPermission());
        // }
        // }
        // }
        // }
        // }
    }

    /** Cleans auth cache after update permissions. */
    public void clearAllCache() {
        getAuthorizationCache().clear();
        getAuthenticationCache().clear();
    }
}
