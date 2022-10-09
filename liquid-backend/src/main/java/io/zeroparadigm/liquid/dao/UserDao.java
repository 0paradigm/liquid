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

package io.zeroparadigm.liquid.dao;

import io.zeroparadigm.liquid.dao.entity.User;
import io.zeroparadigm.liquid.dao.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Demo dao.
 *
 * @author hezean
 */
@Repository
@EnableCaching
public class UserDao {

    @Autowired
    UserMapper userMapper;

    @Nullable
    @Transactional(rollbackFor = Exception.class)
    public User getByNameAndUpdate(String name) {
        // User user = Objects.requireNonNull(userMapper.findByName(name));
        // user.setUpdateTime(new Date());
        // userMapper.updateById(user);
        // return user;
        return null;
    }
}
