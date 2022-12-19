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
import org.springframework.beans.factory.annotation.Autowired;

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
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String login = token.getUsername();
        UserBO user = authService.findByNameOrMail(login);
        if (user.getId() == -1) {
            throw new UnknownAccountException();
        }
        return new SimpleAuthenticationInfo(user, user.getPassword(), getName());
    }

    /** Cleans auth cache after update permissions. */
    public void clearAllCache() {
        getAuthorizationCache().clear();
        getAuthenticationCache().clear();
    }
}
