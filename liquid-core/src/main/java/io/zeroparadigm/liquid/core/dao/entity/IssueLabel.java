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
import org.springframework.lang.Nullable;

import java.io.Serializable;

/**
 * Issue Label entity.
 *
 * @author matthewleng
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ToString
@TableName("t_ds_issue_label")
public class IssueLabel implements Serializable{

    /**
     * system generated primary key.
     */
    @TableId(value = "id", type = IdType.AUTO)
    @NonNull
    private Integer id;

    /**
     * repo id
     */
    @TableField("repo")
    @NonNull
    private Integer repo;

    /**
     * label name.
     */
    @TableField("name")
    @NonNull
    private String name;

    /**
     * label color.
     */
    @TableField("color")
    @NonNull
    private String color;

    /**
     * label description.
     */
    @TableField("description")
    @Nullable
    private String description;
}
