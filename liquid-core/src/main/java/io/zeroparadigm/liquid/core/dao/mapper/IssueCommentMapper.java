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
import io.zeroparadigm.liquid.core.dao.entity.IssueComment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IssueCommentMapper extends BaseMapper<IssueComment> {

    /**
     * Create new issue comment.
     */
    void createIssueComment(@Param("repo_id") Integer repoId, @Param("issue_id") Integer issueId,
                            @Param("author_id") Integer authorId, @Param("comment") String comment,
                            @Param("created_at") Long createdAt);

    /**
     * Gets issue comment by id.
     */
    List<IssueComment> findByIssueId(@Param("issue_id") Integer issueId);

    List<IssueComment> findByRepoIdAndIssueDisplayId(@Param("repo_id") Integer repoId,
                                                     @Param("issue_display_id") Integer issueDisplayId);

    Integer cntByRepoAndIssueId(@Param("repo_id") Integer repoId, @Param("issue_id") Integer issueId);

    /**
     * Delete issue comment by id.
     */
    void deleteById(@Param("id") Integer id);

    /**
     * Gets issue comment by id.
     */
    IssueComment findById(@Param("id") Integer id);
}
