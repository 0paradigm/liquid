/*******************************************************************************
 *    $$\      $$\                     $$\       $$\
 *    $$ |     \__|                    \__|      $$ |
 *    $$ |     $$\  $$$$$$\  $$\   $$\ $$\  $$$$$$$ |
 *    $$ |     $$ |$$  __$$\ $$ |  $$ |$$ |$$  __$$ |
 *    $$$$$$$$\$$ |\$$$$$$$ |\$$$$$$  |$$ |\$$$$$$$ |
 *    \________\__| \____$$ | \______/ \__| \_______|
 *                       $$ |
 *                       \__|  :: Liquid ::  (c) 2022
 *
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
 *******************************************************************************/

package edu.sustc.liquid.service.impl;

import edu.sustc.liquid.dao.UserDao;
import edu.sustc.liquid.dao.entity.User;
import edu.sustc.liquid.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "users")
public class UserServiceImpl implements UserService {

    @Autowired private UserDao userDao;

    @Override
    @Cacheable
    public String greet(String s) {
//        User user = userDao.getByNameAndUpdate(s);
        return " hi!";
    }
}