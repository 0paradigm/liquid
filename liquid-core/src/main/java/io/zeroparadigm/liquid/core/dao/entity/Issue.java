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
@TableName("t_ds_issue")
public class Issue implements Serializable {

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
     * issue opener.
     */
    @TableField("opener")
    @NonNull
    private Integer opener;

    /**
     * issue created time.
     */
    @TableField("created_at")
    @NonNull
    private Long createdTime;

    /**
     * issue closed
     */
    @TableField("closed")
    @NonNull
    private Boolean closed;
}
