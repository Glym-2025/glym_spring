package glym.glym_spring.global.exception.domain;
import glym.glym_spring.global.exception.errorcode.ErrorCode;
import glym.glym_spring.global.utils.ApplicationContextProvider;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@Getter
public class CustomException extends RuntimeException {

    private static final Logger log = LoggerFactory.getLogger(CustomException.class);
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(resolveMessage(errorCode));
        this.errorCode = errorCode;
        log.error("CustomException thrown: code={}, message={}", errorCode.name(), getMessage());
    }

    private static String resolveMessage(ErrorCode errorCode) {
        try {
            MessageSource messageSource = ApplicationContextProvider.getMessageSource();
            String message = messageSource.getMessage(errorCode.getMessage(), null, LocaleContextHolder.getLocale());
            if (message == null || message.isEmpty()) {
                log.warn("Message not found for code: {}, using default message", errorCode.getMessage());
                return errorCode.getMessage(); // Fallback to message key
            }
            return message;
        } catch (Exception e) {
            log.error("Failed to resolve message for code: {}", errorCode.getMessage(), e);
            return errorCode.getMessage(); // Fallback to message key
        }
    }
}