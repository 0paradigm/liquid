package io.zeroparadigm.liquid.core.service.impl;

import io.zeroparadigm.liquid.common.api.core.UserAuthService;
import io.zeroparadigm.liquid.common.bo.UserBO;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class UserAuthServiceImpl implements UserAuthService {

    @Override
    public UserBO findByNameOrMail(String login) {
        UserBO userBO = new UserBO();
        userBO.setLogin("liquid-official");
        userBO.setId(1);
        userBO.setPassword("liquid");
        userBO.setEmail("admin@liquid.com");
        return userBO;
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
