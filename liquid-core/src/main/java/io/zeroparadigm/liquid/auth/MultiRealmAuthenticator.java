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

package io.zeroparadigm.liquid.auth;

import io.zeroparadigm.liquid.auth.realm.GenericAuthorizationRealm;
import java.util.Optional;
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
public class MultiRealmAuthenticator extends ModularRealmAuthenticator {

    @Override
    protected AuthenticationInfo doAuthenticate(AuthenticationToken authenticationToken) throws AuthenticationException {
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
