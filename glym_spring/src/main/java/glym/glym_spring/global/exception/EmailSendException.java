package glym.glym_spring.global.exception;

import glym.glym_spring.global.exception.errorcode.ErrorCode;

public class EmailSendException extends CustomException{
    public EmailSendException(ErrorCode errorCode, String email) {
        super(errorCode, email);
    }
}
