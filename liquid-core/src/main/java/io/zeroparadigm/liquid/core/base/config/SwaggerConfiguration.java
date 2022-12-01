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

package io.zeroparadigm.liquid.core.base.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.EndpointLinksResolver;
import org.springframework.boot.actuate.endpoint.web.EndpointMapping;
import org.springframework.boot.actuate.endpoint.web.EndpointMediaTypes;
import org.springframework.boot.actuate.endpoint.web.ExposableWebEndpoint;
import org.springframework.boot.actuate.endpoint.web.WebEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Rest API docs generated by Swagger.
 *
 * @author hezean
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
public class SwaggerConfiguration {

    private static final String API_BASE_PACKAGE = "io.zeroparadigm.liquid.core.controller";

    private static final String LICENSE = "Apache-2.0";

    @Value("${application.artifact:liquid}")
    private String appName;

    @Value("${build.version:dev}")
    private String buildVersion;

    @Bean
    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    Docket liquidCoreApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(API_BASE_PACKAGE))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(appName)
                .license(LICENSE)
                .version(buildVersion)
                .build();
    }

    /**
     * ref: <a href="https://github.com/springfox/springfox/issues/3462">springfox#3462</a>.
     */
    @Bean
    WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(
                                                                  WebEndpointsSupplier webEndpointsSupplier,
                                                                  ServletEndpointsSupplier servletEndpointsSupplier,
                                                                  ControllerEndpointsSupplier controllerEndpointsSupplier,
                                                                  EndpointMediaTypes endpointMediaTypes,
                                                                  CorsEndpointProperties corsProperties,
                                                                  WebEndpointProperties webEndpointProperties,
                                                                  Environment environment) {
        List<ExposableEndpoint<?>> allEndpoints = new ArrayList<>();
        Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
        allEndpoints.addAll(webEndpoints);
        allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
        allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());

        String basePath = webEndpointProperties.getBasePath();
        EndpointMapping endpointMapping = new EndpointMapping(basePath);
        boolean shouldRegisterLinksMapping =
                this.shouldRegisterLinksMapping(webEndpointProperties, environment, basePath);
        return new WebMvcEndpointHandlerMapping(
                endpointMapping,
                webEndpoints,
                endpointMediaTypes,
                corsProperties.toCorsConfiguration(),
                new EndpointLinksResolver(allEndpoints, basePath),
                shouldRegisterLinksMapping,
                null);
    }

    private boolean shouldRegisterLinksMapping(
                                               WebEndpointProperties webEndpointProperties, Environment environment,
                                               String basePath) {
        return webEndpointProperties.getDiscovery().isEnabled()
                && (StringUtils.hasText(basePath)
                        || ManagementPortType.get(environment)
                                .equals(ManagementPortType.DIFFERENT));
    }

    @RestController
    public class SwaggerHandler {

        @Autowired(required = false)
        private SecurityConfiguration securityConfiguration;

        @Autowired(required = false)
        private UiConfiguration uiConfiguration;

        @Autowired
        private SwaggerResourcesProvider swaggerResources;

        @GetMapping("/swagger-resources/configuration/security")
        public Mono<ResponseEntity<SecurityConfiguration>> securityConfiguration() {
            return Mono.just(ResponseEntity.ok(Optional.ofNullable(securityConfiguration)
                    .orElse(SecurityConfigurationBuilder.builder().build())));
        }

        @GetMapping("/swagger-resources/configuration/ui")
        public Mono<ResponseEntity<UiConfiguration>> uiConfiguration() {
            return Mono.just(new ResponseEntity<>(
                    Optional.ofNullable(uiConfiguration).orElse(UiConfigurationBuilder.builder().build()),
                    HttpStatus.OK));
        }

        @SuppressWarnings("rawtypes")
        @GetMapping("/swagger-resources")
        public Mono<ResponseEntity> swaggerResources() {
            return Mono.just(ResponseEntity.ok(swaggerResources.get()));
        }
    }
}
