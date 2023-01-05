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

package io.zeroparadigm.liquid.core.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.zeroparadigm.liquid.core.dao.entity.Repo;
import io.zeroparadigm.liquid.core.dao.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * User mapper.
 *
 * @author hezean
 */
@Mapper
@EnableCaching
public interface UserMapper extends BaseMapper<User> {

    /**
     * Gets user by id.
     *
     * @param id liquid id
     * @return the user, null if id is invalid
     */
    @Nullable
    User findById(@Nullable @Param("id") Integer id);

    List<User> listAll();

    boolean hasAccessTo(@Param("uid") Integer userId, @Param("owner") String owner, @Param("repo") String repoName);

    /**
     * create user.
     */
    void createUser(@Param("login") String login, @Param("name") String name, @Param("email") String email,
                    @Nullable @Param("twitter_username") String twitter_username, @Nullable @Param("bio") String bio,
                    @Nullable @Param("company") String company, @Nullable @Param("location") String location,
                    @Param("password") String password, @Nullable @Param("phone") String phone);

    /**
     * update user
     * @param phone
     * @return
     */
    void updateUserById(@Param("id") Integer id, @Nullable @Param("twitter_username") String twitter_username,
                    @Nullable @Param("bio") String bio, @Nullable @Param("company") String company, @Nullable @Param("name") String name,
                    @Nullable @Param("location") String location, @Nullable @Param("phone") String phone,
                        @Param("updated_at") Long updated_at);

    @Nullable
    User findByPhone(@Nullable @Param("phone") String phone);

    /**
     * Deletes user by id.
     *
     * @param id liquid id
     */
    void deleteById(@NonNull @Param("id") Integer id);

    /**
     * Gets first user with target name or mail.
     *
     * @param nameOrMail the user's name or email
     * @return the first user matched
     */
    @Nullable
    User findByNameOrMail(@Param("name_or_mail") String nameOrMail);

    /**
     * Stars a repo.
     *
     * @param login  user's login
     * @param repoId repo's id
     */
    void starRepo(@Param("user") String login, @Param("repo") Integer repoId);

    /**
     * Undo star a repo.
     *
     * @param login  user's login
     * @param repoId repo's id
     */
    void unstarRepo(@Param("user") String login, @Param("repo") Integer repoId);

    /**
     * Watch a repo
     *
     * @param login         user's login
     * @param repoId        repo's id
     * @param participation participation
     * @param issues        issues
     * @param pulls         pulls
     * @param releases      releases
     * @param discussions discussions
     * @param security_alerts securtiy alerts
     */
    void watchRepo(@Param("user") String login, @Param("repo") Integer repoId,
                   @Param("participation") Boolean participation, @Param("issues") Boolean issues,
                   @Param("pulls") Boolean pulls, @Param("releases") Boolean releases,
                   @Param("discussions") Boolean discussions, @Param("security_alerts") Boolean security_alerts);

    /**
     * Unwatch a repo
     */
    void unwatchRepo(@Param("user") String login, @Param("repo") Integer repoId);


    /**
     * List repos of the user.
     *
     * @param login user's login
     * @return all repo of this user
     */
    @Nullable
    List<Repo> listUserRepos(@Param("user") String login);

    List<Repo> listStarredRepos(@Param("user") Integer user);

    /**
     * Fuzzy search using login or main.
     */
    @Nullable
    List<User> fuzzySearch(@Param("nameOrMail") String nameOrMail);
}
