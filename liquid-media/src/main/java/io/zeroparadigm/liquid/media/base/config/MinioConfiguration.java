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

package io.zeroparadigm.liquid.media.base.config;

import io.minio.MinioClient;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@RefreshScope
@Slf4j
public class MinioConfiguration {

    @Value("${minio.endpoint}")
    private String endPoint;

    @Value("${minio.root-user}")
    private String rootUser;

    @Value("${minio.root-password}")
    private String rootPassword;

    @Value("${minio.timeout}")
    private long timeout;

    @Bean
    MinioClient minioClient() {
        log.info("Building Minio client [endpoint: {}, user: {}, timeout: {}ms]",
                endPoint, rootUser, timeout);
        return MinioClient.builder()
                .endpoint(endPoint)
                .credentials(rootUser, rootPassword)
                .httpClient(
                        new OkHttpClient.Builder()
                                .callTimeout(timeout, TimeUnit.MILLISECONDS)
                                .build())
                .build();
    }
}
