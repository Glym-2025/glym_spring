package glym.glym_spring.global.exception.domain;

import glym.glym_spring.global.exception.errorcode.ErrorCode;
import lombok.Getter;

@Getter
public class EmailException extends RuntimeException {

    private final ErrorCode errorCode;
    private String email;

    public EmailException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public EmailException(ErrorCode errorCode, String email){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.email = email;
    }
}
