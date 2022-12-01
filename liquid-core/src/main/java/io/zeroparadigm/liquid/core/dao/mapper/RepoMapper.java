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
    User findByOwnerAndName(@Param("owner") String owner, @Param("name") String name);

    /**
     * Gets repo "owner/name".
     *
     * @param ownerId owner's id
     * @param name repo name
     * @return the repo entity, or null
     */
    @Nullable
    User findByOwnerIdAndName(@Param("owner_id") Integer ownerId, @Param("name") String name);

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
}
