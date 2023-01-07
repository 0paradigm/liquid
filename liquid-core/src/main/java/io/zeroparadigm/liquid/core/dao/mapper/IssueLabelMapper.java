package io.zeroparadigm.liquid.core.dao.mapper;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.zeroparadigm.liquid.core.dao.entity.IssueLabel;
import io.zeroparadigm.liquid.core.dao.entity.IssueLabelDisp;
import io.zeroparadigm.liquid.core.dao.entity.Repo;
import io.zeroparadigm.liquid.core.dao.entity.User;
import io.zeroparadigm.liquid.core.dao.entity.Issue;
import io.zeroparadigm.liquid.core.dao.entity.IssueLabel;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Issue mapper.
 *
 * @author matthewleng
 */
@Mapper
@EnableCaching
public interface IssueLabelMapper extends BaseMapper<IssueLabel> {

    /**
     * Create new issue label.
     */
    void createIssueLabel(@Param("repo_id") Integer repoId, @Param("name") String name,
                          @Param("color") String color, @Param("description") String description);

    List<IssueLabelDisp> listAllLabelsOfIssue(@Param("repo_id") Integer repoId, @Param("issue_display_id") Integer issue_display_id);

    /**
     * Gets issue label by id.
     *
     * @param id issue label id
     * @return the issue label entity, null if id does not exist
     */
    @Nullable
    IssueLabel findById(@Nullable @Param("id") Integer id);

    /**
     * Delete issue label by id.
     *
     * @param id issue label id
     */
    void deleteById(@Nullable @Param("id") Integer id);

    /**
     * Gets issue labels by repo id.
     *
     * @param repoId repo's id
     */
    @Nullable
    List<IssueLabel> findByRepoId(@Param("repo_id") Integer repoId);

    /**
     * Delete issue label from repo.
     *
     * @param repoId repo's id
     * @param name   issue label's name
     */
    void deleteIssueLabel(@Param("repo_id") Integer repoId, @Param("name") String name);

    /**
     *
     * Get issue label by repo id and name.
     * @param repoId repo's id
     * @param name issue label's name
     * @return issue label entity
     */
    @Nullable
    IssueLabel findByRepoIdAndName(@Param("repo_id") Integer repoId, @Param("name") String name);
}
