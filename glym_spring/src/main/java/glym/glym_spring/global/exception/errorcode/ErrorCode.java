package glym.glym_spring.global.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.util.Locale;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 공통 오류
    INVALID_REQUEST(HttpStatus.NOT_FOUND, "error.invalid.request"),
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "error.unknown"),

    // 파일 관련 오류
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "file.not.found"),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "file.name.invalid"),
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, "file.format.invalid"),
    IO_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "file.io.error"),

    // 이미지 관련 오류
    INVALID_IMAGE(HttpStatus.BAD_REQUEST, "image.invalid"),
    TEXT_SIZE_TOO_SMALL(HttpStatus.BAD_REQUEST, "image.text.size.tooSmall"),
    SENTENCE_SIZE_TOO_SMALL(HttpStatus.BAD_REQUEST, "image.sentence.size.tooSmall"),
    SENTENCE_SIZE_TOO_LARGE(HttpStatus.BAD_REQUEST, "image.sentence.size.tooLarge"),
    RESOLUTION_TOO_LOW(HttpStatus.BAD_REQUEST, "image.resolution.tooLow"),
    RESOLUTION_UNKNOWN(HttpStatus.BAD_REQUEST, "image.resolution.unknown"),
    CONTRAST_ERROR(HttpStatus.BAD_REQUEST, "image.contrast.invalid"),
    TILT_ERROR(HttpStatus.BAD_REQUEST, "image.tilt.invalid"),
    MARGIN_ERROR(HttpStatus.BAD_REQUEST, "image.margin.invalid"),
    IMAGE_SAVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "image.save.error"),

    // 유저 관련 오류
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "user.find.notFound"),
    USER_NOT_AUTHENTICATED(HttpStatus.UNAUTHORIZED, "auth.user.notAuthenticated"),
    USER_INVALID_PRINCIPAL(HttpStatus.UNAUTHORIZED, "auth.user.invalidPrincipal"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "auth.email.alreadyExists"),
    PHONENUMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "auth.phone.alreadyExists"),

    // 이메일 전송 오류
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "email.send.failed"),

    // 인증 코드 오류
    EMAIL_CODE_MISMATCH(HttpStatus.FORBIDDEN, "email.code.mismatch"),
    EMAIL_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "email.code.notFound"),

    // 토큰 오류
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "token.refresh.invalid"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "token.refresh.notFound"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "token.refresh.expired"),
    REFRESH_TOKEN_MISMATCH(HttpStatus.FORBIDDEN, "token.refresh.mismatch"),
    JOB_NOT_FOUND(HttpStatus.NOT_FOUND, "job.find.notFound"),

    // AI 서버 관련 오류
    AI_SERVER_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "ai.server.unavailable"),
    AI_SERVER_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "ai.server.timeout"),
    AI_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ai.server.error"),
    AI_CONNECTION_ERROR(HttpStatus.REQUEST_TIMEOUT, "ai.server.connection.error"),
    FONT_NAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "font.name.alreadyExists"),
    FONT_CREATION_LIMIT_EXCEEDED(HttpStatus.FORBIDDEN, "font.creation.limit.exceeded");
    private final HttpStatus status;
    private final String message;

    public String getMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(message,null,locale);
    }
}