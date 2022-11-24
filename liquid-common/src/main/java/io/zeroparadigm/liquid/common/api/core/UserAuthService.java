package io.zeroparadigm.liquid.common.api.core;

import io.zeroparadigm.liquid.common.bo.UserBO;

public interface UserAuthService {
    
    public UserBO findByNameOrMail(String login);

    public Object getPassword();

    public UserBO findById(Integer userId);
}
