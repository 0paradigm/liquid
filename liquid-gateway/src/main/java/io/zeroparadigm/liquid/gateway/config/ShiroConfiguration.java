package io.zeroparadigm.liquid.gateway.config;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.zeroparadigm.liquid.gateway.MultiRealmAuthenticator;
import io.zeroparadigm.liquid.gateway.filters.NoRedirectLogoutFilter;
import io.zeroparadigm.liquid.gateway.filters.RestJwtTokenVerifyFilter;
import io.zeroparadigm.liquid.gateway.realm.GenericAuthorizationRealm;
import io.zeroparadigm.liquid.gateway.realm.JwtVerifyRealm;
import io.zeroparadigm.liquid.gateway.realm.UserPasswordRealm;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.Filter;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.http.codec.ServerCodecConfigurer;

/**
 * Shiro config.
 *
 * @author hezean
 */
@Slf4j
@Configuration
public class ShiroConfiguration {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Bean
    MultiRealmAuthenticator multiRealmAuthenticator() {
        MultiRealmAuthenticator authenticator = new MultiRealmAuthenticator();
        authenticator.setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());
        return authenticator;
    }

    @Bean
    AuthorizingRealm userPasswordRealm() {
        return enableCached(new UserPasswordRealm());
    }

    @Bean
    AuthorizingRealm jwtVerifyRealm() {
        return enableCached(new JwtVerifyRealm());
    }

    @Bean
    AuthorizingRealm genericAuthorizationRealm() {
        return enableCached(new GenericAuthorizationRealm());
    }

    @Bean
    DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setAuthenticator(multiRealmAuthenticator());

        List<Realm> realms = new LinkedList<>();
        realms.add(genericAuthorizationRealm());
        realms.add(userPasswordRealm());
        realms.add(jwtVerifyRealm());

        manager.setRealms(realms);
        manager.setRememberMeManager(rememberMeManager());
        manager.setSessionManager(sessionManager());
        manager.setCacheManager(ehCacheManager());
        return manager;
    }

    @Bean
    RememberMeManager rememberMeManager() {
        return new CookieRememberMeManager();
    }

    @Bean
    SessionManager sessionManager() {
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

    private static final EhCacheManager CACHE_MANAGER_INSTANCE = new EhCacheManager();

    static {
        CACHE_MANAGER_INSTANCE.setCacheManagerConfigFile("classpath:auth/ehcache.xml");
    }

    @Bean
    EhCacheManager ehCacheManager() {
        return CACHE_MANAGER_INSTANCE;
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
    ShiroFilterFactoryBean shiroFilterFactoryBean(ResourceLoader resourceLoader) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();

        bean.setSecurityManager(securityManager());

        Map<String, Filter> filters = new LinkedHashMap<>();
        filters.put("authc", new RestJwtTokenVerifyFilter());
        filters.put("logout", new NoRedirectLogoutFilter());

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
        log.info("Shiro config: {}", map);

        bean.setFilterChainDefinitionMap(map);
        return bean;
    }



}