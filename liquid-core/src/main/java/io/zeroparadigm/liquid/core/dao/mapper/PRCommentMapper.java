package io.zeroparadigm.liquid.core.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.zeroparadigm.liquid.core.dao.entity.PRComment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PRCommentMapper extends BaseMapper<PRComment> {

    /**
     * Create new pr comment.
     */
    void createPRComment(@Param("repo_id") Integer repoId, @Param("pr_id") Integer prId,
                         @Param("author_id") Integer authorId, @Param("comment") String comment,
                         @Param("created_at") Long createdAt);

    /**
     * Gets pr comment by pr id.
     */
    List<PRComment> findByPRId(@Param("pr_id") Integer prId);

    /**
     * Delete pr comment by id.
     */
    void deleteById(@Param("id") Integer id);


}
