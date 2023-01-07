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
public interface RepoMapper extends BaseMapper<Repo> {

    /**
     *
     * Create repo.
     *
     */
    @Nullable
    void createRepo(@NonNull @Param("userId") Integer userId, @NonNull @Param("repoName") String repoName,
                    @Param("forkedId") Integer forkedId, @Param("description") String description,
                    @Param("language") String language, @Param("private") Boolean privat);

    List<String> listContributors(@Param("repoId") Integer repoId);

    void addContributor(@Param("repoId") Integer repoId, @Param("userId") String userLogin);

    /**
     * Gets repo by id.
     *
     * @param id repo id
     * @return the repo entity, null if id does not exist
     */
    @Nullable
    Repo findById(@Nullable @Param("id") Integer id);

    /**
     * Deletes repo by id.
     *
     * @param id repo id
     */
    void deleteById(@NonNull @Param("id") Integer id);

    /**
     * Gets repo "owner/name".
     *
     * @param owner owner's login
     * @param name repo name
     * @return the repo entity, or null
     */
    @Nullable
    Repo findByOwnerAndName(@Param("owner") String owner, @Param("name") String name);

    void updateNameFindByOwnerAndName(@Param("owner") String owner, @Param("name") String name,
                                      @Param("newName") String newName);

    /**
     * Gets repo "owner/name".
     *
     * @param ownerId owner's id
     * @param name repo name
     * @return the repo entity, or null
     */
    @Nullable
    Repo findByOwnerIdAndName(@Param("owner_id") Integer ownerId, @Param("name") String name);

    /**
     *
     * Gets repo by name.
     * @param name repo name
     * @return the repo entity, or null
     */
    @Nullable
    List<Repo> findByName(@Param("userId") Integer userId, @Param("name") String name);

    /**
     * Count starer.
     *
     * @param repoId repo's id
     * @return number of starer
     */
    @Nullable
    Integer countStarers(@Param("id") Integer repoId);

    /**
     * List starers.
     *
     * @param repoId repo's id
     * @return list of starer's
     */
    @Nullable
    List<User> listStarers(@Param("id") Integer repoId);

    void addStarer(@Param("repoId") Integer repoId, @Param("userId") Integer userId);

    void deleteStarer(@Param("repoId") Integer repoId, @Param("userId") Integer userId);

    /**
     *  Count watchers.
     */
    @Nullable
    Integer countWatchers(@Param("id") Integer repoId);

    void addWatcher(@Param("repoId") Integer repoId, @Param("userId") Integer userId);

    void removeWatcher(@Param("repoId") Integer repoId, @Param("userId") Integer userId);

    /**
     * List watchers.
     */
    @Nullable
    List<User> listWatchers(@Param("id") Integer repoId);

    /**
     * Count forks.
     */
    @Nullable
    Integer countForks(@Param("id") Integer repoId);

    /**
     * List forks.
     */
    @Nullable
    List<Repo> listForks(@Param("id") Integer repoId);

    /**
     * repo authorities manager
     */
    void setAuth(@Param("repoId") Integer repoId, @Param("userId") Integer userId,
                 @Param("read") Boolean read, @Param("manage") Boolean manage,
                 @Param("push") Boolean push, @Param("settings") Boolean settings,
                 @Param("admin") Boolean admin);

    /**
     * add collaborator
     */
    void addCollaborator(@Param("repoId") Integer repoId, @Param("userId") Integer userId);

    void getIsPrivate(@Param("owner") String login, @Param("name") String repo);

    void setIsPrivate(@Param("owner") String login, @Param("name") String repo, @Param("isPrivate") Boolean isPrivate);

    /**
     * remove collaborator
     */
    void removeCollaborator(@Param("repoId") Integer repoId, @Param("userId") Integer userId);

    /**
     * list collaborator
     */
    List<User> listCollaborators(@Param("repoId") Integer repoId);

    /**
     * Set repo public.
     * @param repoId repo id
     */
    void setPublic(@Param("repoId") Integer repoId);

    /**
     * Set repo private.
     * @param repoId repo id
     */
    void setPrivate(@Param("repoId") Integer repoId);

    /**
     * Verify user authorization
     * @param repoId repo id
     * @return true if user has authorization
     */
    Boolean verifyAuth(@Param("repoId") Integer repoId, @Param("userId") Integer userId);

    // Todo: Implement after Contributor implementation
    // /**
    // * Count contributors.
    // */
    // @Nullable
    // Integer countContributors(@Param("id") Integer repoId);
    //
    // /**
    // * List contributors.
    // */
    // @Nullable
    // List<User> listContributors(@Param("id") Integer repoId);
}
