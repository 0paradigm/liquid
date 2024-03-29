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

package io.zeroparadigm.liquid.core.dao.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import io.zeroparadigm.liquid.core.dao.entity.User;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

@MybatisTest
@Transactional
@DirtiesContext
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testSimple() {
        User user = userMapper.findByNameOrMail("liquid-official");
        assertThat(user).isNotNull();
        userMapper.deleteById(user.getId());
        assertThat(userMapper.findByNameOrMail("liquid-official")).isNull();
        assertThat(userMapper.findByNameOrMail("foasjcnasdlkvajblvijebo")).isNull();
    }
}
