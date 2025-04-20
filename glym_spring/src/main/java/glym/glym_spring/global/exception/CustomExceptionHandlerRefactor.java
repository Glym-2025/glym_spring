package glym.glym_spring.global.exception;

import glym.glym_spring.global.dto.ApiResponse;
import glym.glym_spring.global.exception.errorcode.ErrorCodeRefactor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandlerRefactor {

    @ExceptionHandler(CustomExceptionRefactor.class)
    public ResponseEntity<ApiResponse<EmailErrorResponseRefactor>> handleCustomException(CustomExceptionRefactor e){
        log.error("CustomException 발생: {}", e.getMessage(), e);

        ErrorCodeRefactor errorCode = e.getErrorCode();

        EmailErrorResponseRefactor response = EmailErrorResponseRefactor.builder()
                .email(e.getEmail())
                .errorCode(errorCode)
                .errorMessage(errorCode.getErrorMessage())
                .build();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(response, errorCode.getStatus().value(), errorCode.getMessage()));
    }
}
