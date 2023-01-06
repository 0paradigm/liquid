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

