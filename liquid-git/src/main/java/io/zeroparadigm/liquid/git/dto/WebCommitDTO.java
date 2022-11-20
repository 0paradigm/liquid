package io.zeroparadigm.liquid.git.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestPart;

@Data
public class WebCommitDTO {

    String taskId;

    String message;

    List<String> addFiles;
}
