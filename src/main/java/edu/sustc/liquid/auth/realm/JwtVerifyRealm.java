/*******************************************************************************
 *    $$\      $$\                     $$\       $$\
 *    $$ |     \__|                    \__|      $$ |
 *    $$ |     $$\  $$$$$$\  $$\   $$\ $$\  $$$$$$$ |
 *    $$ |     $$ |$$  __$$\ $$ |  $$ |$$ |$$  __$$ |
 *    $$$$$$$$\$$ |\$$$$$$$ |\$$$$$$  |$$ |\$$$$$$$ |
 *    \________\__| \____$$ | \______/ \__| \_______|
 *                       $$ |
 *                       \__|  :: Liquid ::  (c) 2022
 *
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
 *******************************************************************************/

package edu.sustc.liquid.auth.realm;

import edu.sustc.liquid.auth.jwt.JwtToken;
import edu.sustc.liquid.auth.jwt.JwtUtils;
import edu.sustc.liquid.dao.entity.User;
import edu.sustc.liquid.dao.mapper.UserMapper;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Confirms JWT token in header and set the @code{@link User} as principle.
 *
 * @author hezean
 */
@Slf4j
public class JwtVerifyRealm extends GenericAuthorizationRealm {

    @Autowired JwtUtils jwtUtils;

    @Autowired UserMapper userMapper;

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
        User user = userMapper.findById(jwtUtils.getUserId(token));
        if (Objects.isNull(user) || !jwtUtils.verify(token)) {
            throw new AuthenticationException("invalid jwt token");
        }
        return new SimpleAuthenticationInfo(user, token, getName());
    }
}
