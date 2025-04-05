package glym.glym_spring.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String email;

    public CustomException(ErrorCode errorCode, String email){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.email = email;
    }

}
