package glym.glym_spring.domain.aiserverclient.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AIProcessingRequest {
    private String s3ImageKey;
    private String fontName;
    private Long userId;
    private String callbackUrl;
    private Long jobId;

    //create 타임,userId, 작업상태

}