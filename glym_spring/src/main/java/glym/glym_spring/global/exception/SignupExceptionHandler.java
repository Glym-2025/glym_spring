package glym.glym_spring.global.exception;

import glym.glym_spring.global.dto.ApiResponse;
import glym.glym_spring.global.exception.domain.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
//회원가입시 발생할 수 있는 에러들
public class SignupExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<EmailErrorResponse>> handleCustomException(CustomException e){
        log.error("CustomException 발생: {}", e.getMessage(), e);

        ErrorCode errorCode = e.getErrorCode();

        EmailErrorResponse response = EmailErrorResponse.builder()
                .email(e.getEmail())
                .errorCode(errorCode)
                .errorMessage(errorCode.getMessage())
                .build();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(response, errorCode.getStatus().value(), errorCode.getMessage()));
    }

}
