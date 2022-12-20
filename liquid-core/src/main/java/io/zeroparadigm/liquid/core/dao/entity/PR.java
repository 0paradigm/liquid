package io.zeroparadigm.liquid.core.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * PR entity.
 *
 * @author matthewleng
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ToString
@TableName("t_ds_pr")
public class PR {

    /**
     * system generated primary key.
     */
    @TableId(value = "id", type = IdType.AUTO)
    @NonNull
    private Integer id;

    /**
     * display id
     */
    @TableField("display_id")
    @NonNull
    private Integer displayId;

    /**
     * repo id, fk.
     */
    @TableField("repo")
    @NonNull
    private Integer repo;

    /**
     * pr opener.
     */
    @TableField("opener")
    @NonNull
    private Integer opener;

    /**
     * pr title.
     */
    @TableField("title")
    @NonNull
    private String title;

    /**
     * pr head repo.
     */
    @TableField("head")
    @NonNull
    private Integer head;

    /**
     * pr base repo.
     */
    @TableField("base")
    @NonNull
    private Integer base;

    /**
     * pr created time.
     */
    @TableField("created_at")
    @NonNull
    private Long createdAt;

    /**
     * pr closed time.
     */
    @TableField("closed_at")
    @NonNull
    private Long closedAt;

    /**
     * pr closed status.
     */
    @TableField("closed")
    @NonNull
    private Boolean closed;

}
