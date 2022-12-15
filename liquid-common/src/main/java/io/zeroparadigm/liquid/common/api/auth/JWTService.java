package io.zeroparadigm.liquid.common.api.auth;

import org.springframework.stereotype.Service;

@Service
public interface JWTService {

    /**
     * Get User Id via JWT
     *
     * @param jwt JWT in request
     * @return user Id
     */
    Integer getUserId(String jwt);
}
