package io.zeroparadigm.liquid.gateway.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.zeroparadigm.liquid.gateway.realm.UserPasswordRealm;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.shiro.realm.AuthorizingRealm;

/**
 * Supported login methods.
 *
 * @author hezean
 */
@AllArgsConstructor
@Getter
public enum LoginType {
    PASSWORD("password", UserPasswordRealm.class),
    GITHUB("github", UserPasswordRealm.class),
    PHONE_CAPTCHA("phone-captcha", UserPasswordRealm.class),
    WECHAT("wechat", UserPasswordRealm.class),
    ;

    private final String identifier;
    private final Class<? extends AuthorizingRealm> realm;

    private static final Map<String, LoginType> NAMES_MAP =
        Arrays.stream(LoginType.values())
            .collect(Collectors.toMap(lt -> lt.identifier, lt -> lt));

    @JsonCreator
    public static LoginType forValue(String value) {
        return NAMES_MAP.get(value);
    }

    @JsonValue
    public String toValue() {
        return identifier;
    }

}
