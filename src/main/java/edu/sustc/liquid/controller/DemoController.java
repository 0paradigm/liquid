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

package edu.sustc.liquid.controller;

import edu.sustc.liquid.base.constants.ServiceStatus;
import edu.sustc.liquid.exceptions.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Testing swagger.
 *
 * @author hezean
 */
@Api(value = "this class is only for testing swagger")
@RestController
@RequestMapping("/demo")
@Slf4j
public class DemoController {

    /**
     * Says hello to the person.
     *
     * @param name person name
     * @return a greeting message
     */
    @ApiOperation(value = "hello", notes = "says hello to the person")
    @ApiImplicitParams({
        @ApiImplicitParam(
                name = "name",
                value = "USER_NAME",
                required = true,
                dataTypeClass = String.class,
                example = "chris")
    })
    @GetMapping(value = "/hello")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiException(ServiceStatus.SUCCESS)
    public Object hello(
            @RequestParam(value = "name") String name,
            @ApiIgnore @RequestBody(required = false) String ts) {
        log.debug("User {}", name);
        return "Hello " + name + "!";
    }
}
