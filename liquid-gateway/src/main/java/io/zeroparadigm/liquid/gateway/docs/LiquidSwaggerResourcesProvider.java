package io.zeroparadigm.liquid.gateway.docs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

@Primary
@Component
@RequiredArgsConstructor
public class LiquidSwaggerResourcesProvider implements SwaggerResourcesProvider {

    private static final String SWAGGER2_URL = "/v2/api-docs";

    private static final String LIQUID_PREFIX = "liquid-";

    private final RouteLocator routeLocator;

    @Value("${spring.application.name}")
    private String gatewayName;

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();
        Set<String> routeHosts = new HashSet<>();
        routeLocator.getRoutes()
            .filter(route -> Objects.nonNull(route.getUri().getHost()))
            .filter(route -> !gatewayName.equals(route.getUri().getHost()))
            .map(route -> route.getUri().getHost())
            .subscribe(routeHosts::add);

        for (String routeHost : routeHosts) {
            String serviceUrl = "/" + routeHost.split(LIQUID_PREFIX)[1] + SWAGGER2_URL;
            SwaggerResource swaggerResource = new SwaggerResource();
            swaggerResource.setUrl(serviceUrl);
            swaggerResource.setName(routeHost);
            swaggerResource.setSwaggerVersion("3.0.0");
            resources.add(swaggerResource);
        }
        return resources;
    }
}
