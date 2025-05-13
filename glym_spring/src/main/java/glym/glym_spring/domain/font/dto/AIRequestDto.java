package glym.glym_spring.domain.font.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AIRequestDto {
    private String s3ImageKey;
    private String fontName;
    private Long userId;
    private String callbackUrl;
    private String  jobId;

    //create 타임,userId, 작업상태
}