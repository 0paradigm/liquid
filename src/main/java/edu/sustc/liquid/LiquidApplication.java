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

package edu.sustc.liquid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

/**
 * Liquid code hosting platform (backend).
 *
 * @author hezean
 * @author buzzy0423
 * @author matthewleng2002
 * @author 1tang1
 */
@SpringBootApplication
@Slf4j
public class LiquidApplication {
    private static final String BANNER =
            """
        Liquid Backend running on port {}
        $$\\      $$\\                     $$\\       $$\\
        $$ |     \\__|                    \\__|      $$ |
        $$ |     $$\\  $$$$$$\\  $$\\   $$\\ $$\\  $$$$$$$ |
        $$ |     $$ |$$  __$$\\ $$ |  $$ |$$ |$$  __$$ |
        $$$$$$$$\\$$ |\\$$$$$$$ |\\$$$$$$  |$$ |\\$$$$$$$ |
        \\________\\__| \\____$$ | \\______/ \\__| \\_______|
                           $$ |
                           \\__|  ({}) :: {}""";

    @Value("${application.name}")
    private String appName;

    @Value("${build.version}")
    private String buildVersion;

    @Value("${server.port}")
    private String serverPort;

    @EventListener
    public void run(ApplicationReadyEvent readyEvent) {
        log.info(BANNER, serverPort, appName, buildVersion);
    }

    public static void main(String[] args) {
        SpringApplication.run(LiquidApplication.class, args);
    }
}
