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

package io.zeroparadigm.liquid.core.controller;

import io.zeroparadigm.liquid.core.dao.entity.Repo;
import io.zeroparadigm.liquid.core.dao.entity.User;
import io.zeroparadigm.liquid.core.dao.mapper.RepoMapper;
import io.zeroparadigm.liquid.core.dao.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/internal/v1")
public class InternalController {

    @Autowired
    UserMapper userMapper;

    @Autowired
    RepoMapper repoMapper;

    @GetMapping("/auth/passwd")
    @ResponseBody
    @Nullable
    public String getPasswd(
                            @RequestParam String username) {
        User user = userMapper.findByNameOrMail(username);
        if (user == null) {
            return null;
        }
        return user.getPassword();
    }

    @GetMapping("/auth/writeable")
    @ResponseBody
    public boolean isWritable(
                              @RequestParam String username,
                              @RequestParam String repoFullName) {
        try {
            User user = userMapper.findByNameOrMail(username);
            String[] repoFullNameSplit = repoFullName.split("/");
            Repo repo = repoMapper.findByOwnerAndName(repoFullNameSplit[0], repoFullNameSplit[1]);

            return repoMapper.verifyAuth(repo.getId(), user.getId()) ||
                    repo.getOwner().equals(user.getId());
        } catch (Exception e) {
            log.error("error verifying auth", e);
            return false;
        }
    }

    @GetMapping("/auth/readable")
    @ResponseBody
    public boolean isReadable(
                              @RequestParam String username,
                              @RequestParam String repoFullName) {
        try {
            User user = userMapper.findByNameOrMail(username);
            String[] repoFullNameSplit = repoFullName.split("/");
            Repo repo = repoMapper.findByOwnerAndName(repoFullNameSplit[0], repoFullNameSplit[1]);

            boolean isWritable = repoMapper.verifyAuth(repo.getId(), user.getId()) ||
                    repo.getOwner().equals(user.getId());
            boolean isReadable = !repo.getPrivated() || isWritable;

            return isReadable;
        } catch (Exception e) {
            log.error("error verifying auth", e);
            return false;
        }
    }
}
