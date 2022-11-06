package io.zeroparadigm.liquid.common.api.core.impl;

import io.zeroparadigm.liquid.common.api.core.UserAuthService;
import io.zeroparadigm.liquid.common.bo.UserBO;

public class UserAuthServiceImpl implements UserAuthService {

    @Override
    public UserBO findByNameOrMail(String login) {
        return null;
    }

    @Override
    public Object getPassword() {
        return null;
    }

    @Override
    public UserBO findById(Integer userId) {
        return null;
    }
}
