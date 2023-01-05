package io.zeroparadigm.liquid.core.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.zeroparadigm.liquid.core.dao.entity.PR;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PrMapper extends BaseMapper<PR> {

    /**
     * Create new pr.
     */
    void createPr(@Param("display_id") Integer displayId, @Param("repo_id") Integer repoId,
                  @Param("opener_id") Integer openerId,
                  @Param("title") String title, @Param("head") Integer head,
                  @Param("base") Integer base, @Param("created_at") Long createdAt);

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
    PR findByRepoIdAndDisplayedId(@Param("repo_id") Integer repoId, @Param("display_id") Integer displayId);

    /**
     * Gets pr by repo id and state.
     */
    List<PR> findByRepoIdAndClosed(@Param("repo_id") Integer repoId, @Param("closed") Boolean closed);

    /**
     * Set pr state.
     */
    void setClosed(@Param("id") Integer id, @Param("closed") Boolean closed);

    /**
     * Set pr closed time.
     */
    void setClosedAt(@Param("id") Integer id, @Param("closed_at") Long closedAt);
}
