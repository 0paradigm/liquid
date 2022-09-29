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

package edu.sustc.liquid.base.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.sustc.liquid.auth.MultiRealmAuthenticator;
import edu.sustc.liquid.auth.RestAuthorizationFilter;
import edu.sustc.liquid.auth.realm.GenericAuthorizationRealm;
import edu.sustc.liquid.auth.realm.UserPasswordRealm;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.Filter;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * Shiro config.
 *
 * @author hezean
 */
@Configuration
public class ShiroConfiguration {

    @Bean
    public MultiRealmAuthenticator multiRealmAuthenticator() {
        MultiRealmAuthenticator authenticator = new MultiRealmAuthenticator();
        authenticator.setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());
        return authenticator;
    }

    @Bean
    AuthorizingRealm userPasswordRealm() {
        return enableCached(new UserPasswordRealm());
    }

    @Bean
    AuthorizingRealm genericAuthorizationRealm() {
        return enableCached(new GenericAuthorizationRealm());
    }

    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setAuthenticator(multiRealmAuthenticator());

        List<Realm> realms = new LinkedList<>();
        realms.add(genericAuthorizationRealm());
        realms.add(userPasswordRealm());

        manager.setRealms(realms);
        manager.setRememberMeManager(rememberMeManager());
        manager.setSessionManager(sessionManager());
        manager.setCacheManager(ehCacheManager());
        return manager;
    }

    @Bean
    public RememberMeManager rememberMeManager() {
        return new CookieRememberMeManager();
    }

    @Bean
    public SessionManager sessionManager() {
        return new DefaultWebSessionManager();
    }

    private AuthorizingRealm enableCached(AuthorizingRealm realm) {
        realm.setCachingEnabled(true);
        realm.setAuthorizationCachingEnabled(true);
        realm.setAuthorizationCacheName("authorizationCache");
        realm.setAuthenticationCachingEnabled(true);
        realm.setAuthenticationCacheName("authenticationCache");
        return realm;
    }

    @Bean
    public EhCacheManager ehCacheManager() {
        EhCacheManager cacheManager = new EhCacheManager();
        cacheManager.setCacheManagerConfigFile("classpath:auth/ehcache.xml");
        return cacheManager;
    }

    /**
     * Ref: <a href="https://developer.aliyun.com/article/113501">springboot整合shiro-登录认证和权限管理</a>.
     *
     * <p>Reads config from <b>resources/auth/shiro.ini</b>. Note that <b>/**</b> should be always
     * placed as the last item.
     *
     * @return shiro filter factory
     */
    @Bean
    @SuppressWarnings("unchecked")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(
            ObjectMapper mapper, ResourceLoader resourceLoader) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();

        bean.setSecurityManager(securityManager());

        Map<String, Filter> filters = new LinkedHashMap<>();
        filters.put("authc", new RestAuthorizationFilter());
        filters.put("logout", new RestAuthorizationFilter());

        bean.setFilters(filters);

        List<Map<String, String>> rc;
        try {
            Resource authRc = resourceLoader.getResource("classpath:auth/authrc.json");
            InputStream is = authRc.getInputStream();
            rc = mapper.readValue(is, List.class);
        } catch (IOException e) {
            rc = List.of();
        }

        Map<String, String> map = new LinkedHashMap<>();
        rc.stream().sequential().forEach(i -> map.put(i.get("path"), i.get("auth")));

        bean.setFilterChainDefinitionMap(map);
        return bean;
    }
}
