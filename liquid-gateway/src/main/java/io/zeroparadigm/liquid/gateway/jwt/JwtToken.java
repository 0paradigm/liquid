package io.zeroparadigm.liquid.gateway.jwt;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * JWT Token in header.
 *
 * @author hezean
 */
@AllArgsConstructor
@ToString
public class JwtToken implements AuthenticationToken {

    private final String token;

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    @SuppressWarnings("java:S4144")
    public Object getCredentials() {
        return token;
    }
}
