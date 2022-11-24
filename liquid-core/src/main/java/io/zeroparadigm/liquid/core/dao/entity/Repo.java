package io.zeroparadigm.liquid.core.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.springframework.lang.Nullable;

import java.io.Serializable;

/**
 * Repo entity.
 *
 * @author hezean
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ToString
@TableName("t_ds_repo")
public class Repo implements Serializable {

    /**
     * system generated primary key.
     */
    @TableId(value = "id", type = IdType.AUTO)
    @NonNull
    private Integer id;

    /**
     * owner id, fk.
     */
    @TableField("owner")
    @NonNull
    private Integer owner;

    /**
     * repo name.
     */
    @TableField("name")
    @NonNull
    private String name;

    /**
     * forked from (origin) repo id.
     */
    @TableField("forked_from")
    @Nullable
    private Integer forkedFrom;
}
