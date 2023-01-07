package io.zeroparadigm.liquid.core.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import java.io.Serializable;

/**
 * Issue entity.
 *
 * @author matthewleng
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ToString
@TableName("t_ds_repo_milestone")
public class MileStone {

    /**
     * system generated primary key.
     */
    @TableId(value = "id", type = IdType.AUTO)
    @NonNull
    private Integer id;

    /**
     * repo id, fk.
     */
    @TableField("repo")
    @NonNull
    private Integer repo;

    /**
     * milestone name.
     */
    @TableField("name")
    @NonNull
    private String name;

    /**
     * milestone description.
     */
    @TableField("description")
    @NonNull
    private String description;

    /**
     * milestone due date.
     */
    @TableField("due_at")
    private Long dueAt;

    /**
     * milestone state
     */
    @TableField("closed")
    @NonNull
    private boolean closed;

}
