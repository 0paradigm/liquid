/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.zeroparadigm.liquid.core.service.impl;

import io.zeroparadigm.liquid.common.api.core.UserAuthService;
import io.zeroparadigm.liquid.common.bo.UserBO;
import io.zeroparadigm.liquid.core.dao.entity.User;
import io.zeroparadigm.liquid.core.dao.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@DubboService
public class UserAuthServiceImpl implements UserAuthService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserBO findByNameOrMail(String login) {
        var userRec = userMapper.findByNameOrMail(login);
        return UserBO.builder()
                .id(userRec.getId())
                .login(userRec.getLogin())
                .email(userRec.getEmail())
                .password(userRec.getPassword())
                .build();
    }

    @Override
    public UserBO findById(Integer userId) {
        var userRec = userMapper.findById(userId);
        return UserBO.builder()
                .id(userRec.getId())
                .login(userRec.getLogin())
                .email(userRec.getEmail())
                .password(userRec.getPassword())
                .build();

    }
    
    @Override
    public void register(String userName, String userMail, String phone, String userPassword) {
        //TODO: missing mapper function
    }
}
