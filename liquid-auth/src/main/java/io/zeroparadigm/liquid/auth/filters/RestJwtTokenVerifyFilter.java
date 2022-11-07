package io.zeroparadigm.liquid.auth.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.zeroparadigm.liquid.auth.jwt.JwtToken;
import io.zeroparadigm.liquid.common.constants.CommonConsts;
import io.zeroparadigm.liquid.common.enums.ServiceStatus;
import io.zeroparadigm.liquid.common.dto.Result;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

@Configuration
@Slf4j
public class RestJwtTokenVerifyFilter extends BasicHttpAuthenticationFilter {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected boolean isAccessAllowed(
        ServletRequest request, ServletResponse response, Object mappedValue)
        throws UnauthorizedException {
        try {
            executeLogin(request, response);
            return true;
        } catch (Exception e) {
            log.error("Error logging in via jwt token", e);
            return false;
        }
    }

    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        if (request instanceof HttpServletRequest req) {
            return Objects.nonNull(req.getHeader(CommonConsts.JWT_TOKEN_HEADER));
        }
        return true;
    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response)
        throws Exception {
        if (request instanceof HttpServletRequest req) {
            String token = req.getHeader(CommonConsts.JWT_TOKEN_HEADER);
            JwtToken jwtToken = new JwtToken(token);
            getSubject(request, response).login(jwtToken);
            return true;
        }
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response)
        throws IOException {
        if (request instanceof HttpServletRequest req) {
            log.info(
                "[ip: {}, session: {}] access denied for {}",
                SecurityUtils.getSubject().getSession().getHost(),
                SecurityUtils.getSubject().getSession().getId(),
                req.getServletPath());
        }
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        if (response instanceof HttpServletResponse resp) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
        mapper.writeValue(response.getWriter(), Result.error(ServiceStatus.NOT_AUTHENTICATED));
        return false;
    }
}