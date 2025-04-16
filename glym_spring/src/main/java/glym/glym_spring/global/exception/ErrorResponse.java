package glym.glym_spring.global.exception;

import glym.glym_spring.global.exception.errorcode.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import io.swagger.v3.oas.annotations.media.Schema;
@Builder
@Getter

@Schema(description = "에러 응답")
public class ErrorResponse {
    @Schema(description = "에러 코드", example = "INVALID_IMAGE")
    private ErrorCode errorCode;

    @Schema(description = "에러 메시지", example = "유효하지 않은 이미지 파일입니다")
    private String errorMessage;


    public ErrorResponse(ErrorCode errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

}