package glym.glym_spring.global.exception;

import glym.glym_spring.global.exception.errorcode.ErrorCodeRefactor;
import lombok.Getter;

@Getter
public class CustomExceptionRefactor extends RuntimeException {

    private final ErrorCodeRefactor errorCode;
    private String email;

    public CustomExceptionRefactor(ErrorCodeRefactor errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomExceptionRefactor(ErrorCodeRefactor errorCode, String email){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.email = email;
    }

}
