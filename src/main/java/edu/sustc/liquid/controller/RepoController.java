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
import edu.sustc.liquid.dto.Result;
import edu.sustc.liquid.exceptions.ApiException;
import edu.sustc.liquid.service.impl.GitServiceImpl;
import edu.sustc.liquid.service.impl.UserServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Only for test, not final version.
 *
 * @author Lizinan
 */
@Api(value = "This class is for testing")
@RestController
@RequestMapping("/")
@Slf4j
public class RepoController {
    @Autowired
    GitServiceImpl gitService;

    @ApiOperation(value = "repoPage", notes = "main page of a repo")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "name",
            value = "USER_NAME",
            required = true,
            dataTypeClass = String.class,
            example = "bucky"),
        @ApiImplicitParam(
            name = "repo",
            value = "REPO_NAME",
            required = true,
            dataTypeClass = String.class,
            example = "liquid")
    })
    @GetMapping(value = "/{name}/{repo}")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<Map<String, String>> userPage(
        @RequestParam(value = "name") String name,
        @RequestParam(value = "repo") String repo,
        @ApiIgnore @RequestBody(required = false) String ts) {
        log.debug("Repo {}/{}", name, repo);
        Map<String, String> res = new HashMap<>();
        return Result.success(res);
    }

    @ApiOperation(value = "downloadRepo", notes = "download a repo in .zip")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "name",
            value = "USER_NAME",
            required = true,
            dataTypeClass = String.class,
            example = "bucky"),
        @ApiImplicitParam(
            name = "repo",
            value = "REPO_NAME",
            required = true,
            dataTypeClass = String.class,
            example = "liquid")
    })
    @GetMapping(value = "/{name}/{repo}/download")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiException(ServiceStatus.REQUEST_PARAMS_NOT_VALID_ERROR)
    public Result<String> downloadRepo(
        @RequestParam(value = "name") String name,
        @RequestParam(value = "repo") String repo,
        @ApiIgnore @RequestBody(required = false) String ts) {
        log.debug("Download {}/{}", name, repo);
        return Result.success("test");
    }

}
