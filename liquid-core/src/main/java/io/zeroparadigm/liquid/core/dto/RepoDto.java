package io.zeroparadigm.liquid.core.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.lang.Nullable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ToString
public class RepoDto {

    private Integer id;

    private String owner;

    private String name;

    private String description;

    private String language;

    @Nullable
    private String forkedFrom;

    private Boolean privateRepo;

}
