package glym.glym_spring.domain.font.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobStatusResponseDto {
    private String status; // PENDING, PROCESSING, COMPLETED, FAILED
    private String fontUrl; // COMPLETED 시 서명된 URL
    private String errorMessage; // FAILED 시 에러 메시지
}