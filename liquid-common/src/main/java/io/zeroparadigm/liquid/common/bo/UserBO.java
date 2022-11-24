package io.zeroparadigm.liquid.common.bo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
public class UserBO {
    private Integer id;

    private String login;

    private String email;

    private String password;
}
