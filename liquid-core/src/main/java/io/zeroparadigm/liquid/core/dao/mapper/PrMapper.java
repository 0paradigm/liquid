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
import io.zeroparadigm.liquid.core.dao.entity.PR;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PrMapper extends BaseMapper<PR> {

    /**
     * Create new pr.
     */
    void createPr(@Param("display_id") Integer displayId,
                  @Param("repo_id") Integer repoId,
                  @Param("opener_id") Integer openerId,
                  @Param("title") String title,
                  @Param("head") Integer head,
                  @Param("base") Integer base,
                  @Param("head_branch") String headBranch,
                  @Param("base_branch") String baseBranch,
                  @Param("created_at") Long createdAt);

    /**
     * Gets pr by repo id.
     */
    List<PR> findByRepoId(@Param("repo_id") Integer repoId);

    /**
     * Gets pr by user id.
     */
    List<PR> findByUserId(@Param("user_id") Integer userId);

    /**
     * Gets pr by id.
     */
    PR findById(@Param("id") Integer id);

    /**
     * Gets pr by displayed id.
     */
    PR findByRepoIdAndDisplayedId(@Param("repo_id") Integer repoId,
                                  @Param("display_id") Integer displayId);

    /**
     * Gets pr by repo id and state.
     */
    List<PR> findByRepoIdAndClosed(@Param("repo_id") Integer repoId,
                                   @Param("closed") Boolean closed);

    /**
     * Set pr state.
     */
    void setClosed(@Param("id") Integer id, @Param("closed") Boolean closed);

    /**
     * Set pr closed time.
     */
    void setClosedAt(@Param("id") Integer id, @Param("closed_at") Long closedAt);
}
