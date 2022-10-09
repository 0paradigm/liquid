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

package edu.sustc.liquid.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.sustc.liquid.auth.jwt.JwtToken;
import edu.sustc.liquid.base.constants.Constants;
import edu.sustc.liquid.base.constants.ServiceStatus;
import edu.sustc.liquid.dto.Result;
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
import org.springframework.http.MediaType;

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
            return Objects.nonNull(req.getHeader(Constants.JWT_TOKEN_HEADER));
        }
        return true;
    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response)
            throws Exception {
        if (request instanceof HttpServletRequest req) {
            String token = req.getHeader(Constants.JWT_TOKEN_HEADER);
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
