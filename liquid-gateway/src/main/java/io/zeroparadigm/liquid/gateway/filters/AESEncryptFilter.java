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

package io.zeroparadigm.liquid.gateway.filters;

import io.zeroparadigm.liquid.gateway.utils.AESUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AESEncryptFilter implements GlobalFilter {

    private static final String ENCRYPT_SECRET_KEY = "liquid12345678";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        // 获取需要加密的请求头
        String header = headers.getFirst("X-Encrypt-Header");
        if (header != null) {
            // 对请求头进行AES解密
            try {
                header = AESUtils.decrypt(header, ENCRYPT_SECRET_KEY);
            } catch (Exception e) {
                e.printStackTrace();
                return Mono.error(e);
            }
            // 重新设置请求头
            headers.set("X-Encrypt-Header", header);
        }
        return chain.filter(exchange);
    }
}
