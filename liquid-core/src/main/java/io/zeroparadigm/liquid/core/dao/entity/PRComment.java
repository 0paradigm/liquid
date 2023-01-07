package io.zeroparadigm.liquid.core.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * PR comment entity.
 *
 * @author matthewleng
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ToString
@TableName("t_ds_pr_comment")
public class PRComment {

    /**
     * system generated primary key.
     */
    @TableId(value = "id", type = IdType.AUTO)
    @NonNull
    private Integer id;

    /**
     * repo id.
     */
    @TableField("repo")
    @NonNull
    private Integer repo;

    /**
     * pr id.
     */
    @TableField("pr")
    @NonNull
    private Integer pr;

    /**
     * author id.
     */
    @TableField("author")
    @NonNull
    private Integer author;

    /**
     * comment body.
     */
    @TableField("comment")
    @NonNull
    private String comment;

    /**
     * comment created time.
     */
    @TableField("created_at")
    @NonNull
    private String createdAt;
}
