package glym.glym_spring.domain.font.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "요청 성공 응답")
public class SuccessResponse {
    @Schema(description = "성공 메시지", example = "Font creation request accepted successfully")
    private String message;
    private String jobId;

    public SuccessResponse(String message,String jobId) {
        this.message = message;
        this.jobId = jobId;
    }
}