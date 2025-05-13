package glym.glym_spring.global.exception;

import glym.glym_spring.global.dto.ApiResponse;
import glym.glym_spring.global.exception.domain.CustomException;
import glym.glym_spring.global.exception.errorcode.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleCustomException(CustomException e, Locale locale) {
        ErrorCode errorCode = e.getErrorCode();
        log.error("CustomException 발생: {}", errorCode.getMessage(messageSource, locale));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage(messageSource, locale))
                .build();

        // ApiResponse로 래핑하여 일관된 응답 형식 제공
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorResponse, errorCode.getStatus().value(), errorCode.getMessage(messageSource, locale)));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<Object>> handleNullPointerException() {
        log.error("NullPointer Exception 처리 시작");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "NullPointer Exception 발생"));
    }

    @ExceptionHandler(InternalError.class)
    public ResponseEntity<ApiResponse<Object>> handleInternalError() {
        log.error("InternalError 처리 시작");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error"));
    }


    //@NotBlank 관련 예외처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(errorMessage, HttpStatus.BAD_REQUEST.value(), "Validation Failed"));
    }
}
