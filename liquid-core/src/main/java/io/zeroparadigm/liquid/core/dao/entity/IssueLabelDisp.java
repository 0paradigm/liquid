package io.zeroparadigm.liquid.core.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ToString
@TableName("t_ds_v2_issue_label")
public class IssueLabelDisp {
    @TableField("repo_id")
    Integer repoId;

    @TableField("issue_display_id")
    Integer issue_display_id;

    @TableField("label")
    String label;

    @TableField("color")
    String color;
}

