package io.zeroparadigm.liquid.auth.config;

import io.zeroparadigm.liquid.auth.config.provider.UserNameAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author buzzy0423
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Authorized user Service processing class
     */
    @Autowired
    UserDetailsService baseUserDetailService;

    /**
     * Secure Path Filtering
     *
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**", "/js/**", "/fonts/**", "/icon/**", "/images/**", "/favicon.ico");
    }

    /**
     * Release access restrictions for some authentication authorization portal services
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.requestMatchers().antMatchers("/login", "/oauth/authorize", "/oauth/check_token").and().authorizeRequests()
            .anyRequest().authenticated().and().formLogin().loginPage("/login").failureUrl("/login-error")
            .permitAll();
        http.csrf().disable();
    }

    /**
     * Authentication Management Configuration
     *
     * @param auth
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    /**
     * Authorized user information database provider object configuration
     *
     * @return
     */
    @Bean
    public AbstractUserDetailsAuthenticationProvider daoAuthenticationProvider() {
        UserNameAuthenticationProvider authProvider = new UserNameAuthenticationProvider();
        // set userDetailsService
        authProvider.setUserDetailsService(baseUserDetailService);
        // Prohibit hiding user not found exceptions
        authProvider.setHideUserNotFoundExceptions(false);
        // Using BCrypt for password hashing
        authProvider.setPasswordEncoder(new BCryptPasswordEncoder(6));
        return authProvider;
    }
}

