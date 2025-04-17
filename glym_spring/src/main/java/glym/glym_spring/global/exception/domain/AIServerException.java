package glym.glym_spring.global.exception.domain;

import glym.glym_spring.global.exception.errorcode.AiServerErrorCode;
import glym.glym_spring.global.exception.errorcode.ErrorCode;

public class AIServerException extends RuntimeException {

    private final AiServerErrorCode errorCode;

    public AIServerException(AiServerErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}