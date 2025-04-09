package glym.glym_spring.global.exception.domain;

public class AIServerConnectionException extends RuntimeException {
    public AIServerConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public AIServerConnectionException(String message) {
        super(message);
    }
}