package glym.glym_spring.global.exception;

import glym.glym_spring.global.dto.ApiResponse;
import glym.glym_spring.global.exception.errorcode.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class EmailVerificationExceptionHandler {
    @ExceptionHandler(EmailVerificationException.class)
    public ResponseEntity<ApiResponse<EmailErrorResponse>> handleCustomException(EmailVerificationException e) {
        log.error("CustomException 발생: {}", e.getMessage(), e);

        ErrorCode errorCode = e.getErrorCode();

        EmailErrorResponse response = EmailErrorResponse.builder()
                .email(e.getEmail())
                .errorCode(errorCode)
                .errorMessage(errorCode.getMessage())
                .build();

        return ResponseEntity
                .status(errorCode.getStatus())
                    .body(ApiResponse.error(response, errorCode.getStatus().value(), "Email Verification Failed"));
    }
}
