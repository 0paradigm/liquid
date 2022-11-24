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

package io.zeroparadigm.liquid.git;

import io.zeroparadigm.liquid.common.api.media.MinioService;
import io.zeroparadigm.liquid.git.service.impl.GitBasicServiceImpl;
import org.apache.dubbo.config.annotation.DubboService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestPropertySource;

@Component
@TestPropertySource(properties = {
        "dubbo.consumer.scope = local",
        "dubbo.consumer.check = false",
        "dubbo.registry.address = N/A",
})
public class DubboMockFactory {

    @Bean
    @Primary
//    @DubboService
    GitBasicServiceImpl gitBasicService() {
        return new GitBasicServiceImpl();
    }
}
