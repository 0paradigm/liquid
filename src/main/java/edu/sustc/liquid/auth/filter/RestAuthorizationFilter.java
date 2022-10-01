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
import edu.sustc.liquid.base.constants.ServiceStatus;
import edu.sustc.liquid.dto.Result;
import java.io.IOException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.http.MediaType;

/**
 * Suppress redirect after login, provides unified response body.
 *
 * @author hezean
 */
@Slf4j
public class RestAuthorizationFilter extends FormAuthenticationFilter {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response)
            throws IOException {
        if (request instanceof HttpServletRequest req) {
            log.info(
                    "{} access denied for {}",
                    SecurityUtils.getSubject().getSession().getId(),
                    req.getPathInfo());
        }
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        if (response instanceof HttpServletResponse resp) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
        mapper.writeValue(response.getWriter(), Result.error(ServiceStatus.NOT_AUTHENTICATED));
        return false;
    }
}