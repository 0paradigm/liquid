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
     */
    void updateDueById(@Param("id") Integer id);

    /**
     * Delete milestone by id.
     * @param id milestone id
     */
    void deleteById(@Nullable @Param("id") Integer id);
}
