package glym.glym_spring.global.exception;

import glym.glym_spring.global.exception.errorcode.ErrorCode;

public class EmailVerificationException extends CustomException {
    public EmailVerificationException(ErrorCode errorCode, String email) {
        super(errorCode, email);
    }
}
