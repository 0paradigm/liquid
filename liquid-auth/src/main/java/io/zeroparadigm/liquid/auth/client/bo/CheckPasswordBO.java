package io.zeroparadigm.liquid.auth.client.bo;
import lombok.Builder;
import lombok.Data;

/**
 * @author buzzy0423
 */

@Data
@Builder
public class CheckPasswordBO {

    private String userID;

    private String userName;

    private String passWord;

    private String authorities;
}

