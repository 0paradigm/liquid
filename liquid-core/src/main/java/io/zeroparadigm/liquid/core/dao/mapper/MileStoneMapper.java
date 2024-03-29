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
import io.zeroparadigm.liquid.core.dao.entity.MileStone;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Issue milestone mapper.
 *
 * @author matthewleng
 */
@Mapper
@EnableCaching
public interface MileStoneMapper extends BaseMapper<MileStone> {

    /**
     * Create new milestone.
     */
    void createMileStone(@Param("repo_id") Integer repoId, @Param("name") String name,
                         @Param("description") String description, @Param("due_date") Long dueDate,
                         @Param("closed") Boolean closed);

    /**
     * Get repo milestone by id.
     * @param id milestone id
     * @return the milestone entity, null if id does not exist
     */
    @Nullable
    MileStone findById(@Nullable @Param("id") Integer id);

    /**
     * Get repo milestone by repo id.
     * @param repoId repo id
     * @return the list of milestone
     */
    @Nullable
    List<MileStone> findByRepoId(@Param("repo_id") Integer repoId);

    /**
     * Update milestone due by id.
     * @param id milestone id
     * @param due due time
     */
    void updateDueById(@Param("id") Integer id, @Param("due") Long due);

    /**
     * Delete milestone by id.
     * @param id milestone id
     */
    void deleteById(@Nullable @Param("id") Integer id);

}
