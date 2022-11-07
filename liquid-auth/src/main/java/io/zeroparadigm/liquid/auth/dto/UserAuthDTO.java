package io.zeroparadigm.liquid.auth.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * User authentication object
 *
 * @author buzzy0423
 */
@Data
@Accessors(chain = true)
public class UserAuthDTO {
    /**
     * user ID
     */
    private Long userId;

    /**
     * user name(openId、mobile)
     */
    private String username;

    /**
     * status(True:normal；False：banned)
     */
    private Boolean status;
}
