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

package io.zeroparadigm.liquid.ci.slave;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.event.EventListener;

/**
 * Liquid code hosting platform (ci-slave).
 *
 * @author hezean
 */
@SpringBootApplication
@EnableConfigurationProperties
@EnableDiscoveryClient
@EnableDubbo
@Slf4j
public class LiquidCiSlave {

    private static final String BANNER = """
            Liquid CI Slave running on port {}
              _     _             _     _
             | |   (_) __ _ _   _(_) __| |
             | |   | |/ _` | | | | |/ _` |
             | |___| | (_| | |_| | | (_| |
             |_____|_|\\__, |\\__,_|_|\\__,_|
                          |_| :: {} :: {}""";

    @Value("${spring.application.name:liquid-media}")
    private String appName;

    @Value("${build.version:dev}")
    private String version;

    @Value("${server.port:-1}")
    private int serverPort;

    @EventListener
    public void run(ApplicationReadyEvent readyEvent) {
        log.info(BANNER, serverPort, appName, version);
    }

    public static void main(String[] args) {
        SpringApplication.run(LiquidCiSlave.class, args);
    }
}
