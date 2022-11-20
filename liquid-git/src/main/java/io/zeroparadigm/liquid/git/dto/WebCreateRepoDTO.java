package io.zeroparadigm.liquid.git.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebCreateRepoDTO {

    String initBranch;
}
