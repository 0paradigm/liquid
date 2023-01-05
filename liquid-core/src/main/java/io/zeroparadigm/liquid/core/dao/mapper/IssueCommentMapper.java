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

    List<IssueComment> findByRepoIdAndIssueDisplayId(@Param("repo_id") Integer repoId, @Param("issue_display_id") Integer issueDisplayId);

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
