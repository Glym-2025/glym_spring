package glym.glym_spring.domain.font.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobStatusResponseDto {
    private String status; // 예: "IN_PROGRESS", "COMPLETED", "FAILED"
    private String fontUrl; // 완료 시 S3 프리사인드 URL (선택적)
    private String errorMessage; // 실패 시 에러 메시지 (선택적)
    private Long fontId;

    // 생성자, getter, setter
    public JobStatusResponseDto(String status, String fontUrl, String errorMessage,Long fontId) {
        this.status = status;
        this.fontUrl = fontUrl;
        this.errorMessage = errorMessage;
        this.fontId = fontId;
    }

    public String getStatus() { return status; }
    public String getFontUrl() { return fontUrl; }
    public String getErrorMessage() { return errorMessage; }
    public Long getFontId() { return fontId; }
}
