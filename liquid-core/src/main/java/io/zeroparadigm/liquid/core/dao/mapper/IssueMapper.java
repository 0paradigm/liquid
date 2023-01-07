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
import io.zeroparadigm.liquid.core.dao.entity.IssueLabel;
import io.zeroparadigm.liquid.core.dao.entity.User;
import io.zeroparadigm.liquid.core.dao.entity.Issue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Issue mapper.
 *
 * @author matthewleng
 */
@Mapper
@EnableCaching
public interface IssueMapper extends BaseMapper<Issue> {

    /**
     * Create new issue.
     */
    void createIssue(@Param("display_id") Integer displayId, @Param("repo_id") Integer repoId,
                     @Param("opener") Integer opener, @Param("title") String title,
                     @Param("createdTime") Long createdTime,
                     @Param("closed") Boolean closed);

    void addLabel(@Param("repo") Integer repoId, @Param("issue_id") Integer displayId, @Param("tag") String tag,
                  @Param("color") String color);

    /**
     * Gets issue by id.
     *
     * @param id issue id
     * @return the issue entity, null if id does not exist
     */
    @Nullable
    Issue findById(@Nullable @Param("id") Integer id);

    /**
     * Gets issue by owner id.
     *
     * @param ownerId owner's id
     * @return the issue entity, or null
     */
    @Nullable
    List<Issue> findByOwnerId(@Param("owner_id") Integer ownerId);

    /**
     * Gets issue by displayed id.
     *
     * @param displayedId repo's id
     * @param repoId repo's id
     * @return the issue entity, or null
     */
    @Nullable
    Issue findByDisplayedIdandRepoId(@Param("display_id") Integer displayedId, @Param("repo_id") Integer repoId);

    /**
     * Gets issue by repo id.
     *
     * @param repoId repo's id
     * @return the issue entity, or null
     */
    @Nullable
    List<Issue> findByRepoId(@Param("repo_id") Integer repoId);

    /**
     * Gets issue by repo id.
     *
     * @param repoId repo's id
     * @param ownerId owner's id
     * @return the issue entity, or null
     */
    @Nullable
    List<Issue> findByRepoIdandOwnerId(@Param("repo_id") Integer repoId, @Param("owner_id") Integer ownerId);

    /**
     * Gets issue by repo id.
     *
     * @param repoId repo's id
     * @param label issue label
     * @return the issue entity, or null
     */
    @Nullable
    List<Issue> findByRepoIdandLabel(@Param("repo_id") Integer repoId, @Param("label") IssueLabel label);

    /**
     * Gets issue created time
     * @param id issue id
     * @return the issue created time, or null
     */
    @Nullable
    Long findCreatedTimeById(@Param("id") Integer id);

    /**
     * Gets opener id
     * @param repoId repo's id
     * @param closed issue is closed
     * @return the issue entity, or null
     */
    @Nullable
    List<Issue> findByRepoIdandClosed(@Param("repo_id") Integer repoId, @Param("closed") Boolean closed);

    /**
     * Gets issue by user id and closed.
     * @param userId user id
     * @param closed issue is closed
     * @return the issue entity, or null
     */
    @Nullable
    List<Issue> findByUserIdandClosed(@Param("user_id") Integer userId, @Param("closed") Boolean closed);

    /**
     * Gets issue's assignee
     * @param id issue id
     * @return user list
     */
    @Nullable
    List<User> findAssigneeById(@Param("id") Integer id);

    /**
     * Assign issue to assignee
     * @param issueId issue id
     * @param assignee assignee id
     */
    void assignIssue(@Param("issue_id") Integer issueId, @Param("assignee") Integer assignee);

    /**
     * Remove assignment issue to assignee
     * @param issueId issue id
     * @param assignee assignee id
     */
    void removeAssignee(@Param("issue_id") Integer issueId, @Param("assignee") Integer assignee);

    /**
     * Assign issue with issue label
     *
     * @param issueId issue id
     * @param label_id issue label
     */
    void assignLabelById(@Param("issue_id") Integer issueId, @Param("label_id") Integer label_id);

    /**
     * Remove issue label
     *
     * @param issueId issue id
     * @param label_id issue label
     */
    void removeLabelById(@Param("issue_id") Integer issueId, @Param("label_id") Integer label_id);

    /**
     * Close issue
     * @param issueId issue id
     */
    void closeIssue(@Param("issue_id") Integer issueId, @Param("closed") Boolean closed);

    /**
     * Assign issue to milestone
     * @param issueId issue id
     * @param milestoneId milestone id
     */
    void assignMilestone(@Param("issue_id") Integer issueId, @Param("milestone_id") Integer milestoneId);

    /**
     * Remove issue from milestone
     * @param issueId issue id
     * @param milestoneId milestone id
     */
    void removeMilestone(@Param("issue_id") Integer issueId, @Param("milestone_id") Integer milestoneId);

}
