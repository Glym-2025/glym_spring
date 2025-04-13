package glym.glym_spring.domain.aiserverclient.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AIProcessingResponse {
    private Long jonId;
    private String fontS3Path;
    private String status;
}
