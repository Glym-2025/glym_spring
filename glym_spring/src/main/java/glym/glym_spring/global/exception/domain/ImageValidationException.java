package glym.glym_spring.global.exception.domain;
import glym.glym_spring.global.exception.errorcode.ErrorCode;

public class ImageValidationException extends Exception {
    private final ErrorCode error;

    public ImageValidationException(ErrorCode error) {
        super(error.getMessage());
        this.error = error;
    }

    public ErrorCode getError() {
        return error;
    }
}
