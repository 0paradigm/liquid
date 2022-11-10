package io.zeroparadigm.liquid.git.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestPart;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class WebCommitDTO {

    String taskId;

    String message;

    List<String> addFiles;
}
