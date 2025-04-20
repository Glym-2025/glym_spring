package glym.glym_spring.global.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다"),
    PHONENUMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 휴대폰 번호입니다"),

    INVALID_REQUEST(HttpStatus.NOT_FOUND, "유효하지 않은 요청입니다"),

    //이메일 전송 오류
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패했습니다"),

    //인증코드 오류
    EMAIL_CODE_MISMATCH(HttpStatus.FORBIDDEN, "인증코드가 일치하지 않습니다"),
    EMAIL_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이메일에 대한 인증코드가 존재하지 않습니다"),

    //토큰 오류
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 refresh token 입니다"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "refresh token 이 존재하지 않습니다"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "refresh token 이 만료되었습니다"),
    REFRESH_TOKEN_MISMATCH(HttpStatus.FORBIDDEN, "본인의 refresh token 이 아닙니다"),

    //이미지 파일 검증 오류
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "유효하지 않은 파일명입니다."),
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다. PNG 또는 JPEG 형식만 허용됩니다."),
    INVALID_IMAGE(HttpStatus.BAD_REQUEST, "유효하지 않은 이미지 파일입니다."),
    TEXT_SIZE_TOO_SMALL(HttpStatus.BAD_REQUEST, "글자 크기가 너무 작습니다. 최소 128x128 픽셀 이상이어야 합니다."),
    SENTENCE_SIZE_TOO_SMALL(HttpStatus.BAD_REQUEST, "문장 크기가 너무 작습니다. 최소 512 픽셀 이상이어야 합니다."),
    SENTENCE_SIZE_TOO_LARGE(HttpStatus.BAD_REQUEST, "문장 크기가 너무 큽니다. 최대 1024 픽셀 이하이어야 합니다."),
    RESOLUTION_TOO_LOW(HttpStatus.BAD_REQUEST, "해상도가 너무 낮습니다. 최소 300 DPI 이상이어야 합니다."),
    RESOLUTION_UNKNOWN(HttpStatus.BAD_REQUEST, "해상도 정보를 확인할 수 없습니다."),
    CONTRAST_ERROR(HttpStatus.BAD_REQUEST, "대비가 요구사항을 충족하지 않습니다. 글자 ≤100, 배경 ≥200이어야 합니다."),
    TILT_ERROR(HttpStatus.BAD_REQUEST, "이미지 기울기가 요구사항을 충족하지 않습니다. ±5° 이내여야 합니다."),
    MARGIN_ERROR(HttpStatus.BAD_REQUEST, "이미지 여백이 요구사항을 충족하지 않습니다. 5~10px 이어야 합니다."),

    IMAGE_SAVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"이미지 저장 중 오류가 발생했습니다."),
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"알 수 없는 오류가 발생했습니다."),

    IO_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 처리 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

}
