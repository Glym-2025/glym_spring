package glym.glym_spring.global.exception.domain;

import glym.glym_spring.global.exception.errorcode.ErrorCode;


public class AIServerException extends RuntimeException {

    private final ErrorCode errorCode;

    public AIServerException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}