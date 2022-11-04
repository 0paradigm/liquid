package io.zeroparadigm.liquid.auth.client.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author buzzy0423
 */
@Data
@Builder
public class CheckPasswordDTO {

    /**
     * 登录账号
     */
    private String userName;
}
