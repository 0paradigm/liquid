package io.zeroparadigm.liquid.core.service.impl;

import io.zeroparadigm.liquid.common.api.auth.JWTService;
import io.zeroparadigm.liquid.core.jwt.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
@Slf4j
public class JWTServiceImpl implements JWTService {
    @Autowired
    JwtUtils jwtUtils;

    /**
     * 
     * @param jwt JWT in request
     * @return userId in Integer, maybe null
     */
    @Override
    public Integer getUserId(String jwt) {
        return jwtUtils.getUserId(jwt);
    }
}
