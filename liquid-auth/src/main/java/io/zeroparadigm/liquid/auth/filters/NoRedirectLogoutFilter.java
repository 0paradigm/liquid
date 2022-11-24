package io.zeroparadigm.liquid.auth.filters;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.shiro.web.filter.authc.LogoutFilter;

/**
 * Suppress redirect after logout, provides unified response body.
 *
 * @author hezean
 */
public class NoRedirectLogoutFilter extends LogoutFilter {

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        return true;
    }
}