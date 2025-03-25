package gllim.gllim_spring.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NullPointerException.class)
    public String handleNullPointerException() {
        log.error("NullPointer Exception 처리 시작");
        return "NullPointer Exception 핸들링";
    }

    @ExceptionHandler(InternalError.class)
    public String handleInternalError() {
        log.error("InternalError 처리 시작");
        return "InternalError 핸들링";
    }
}
