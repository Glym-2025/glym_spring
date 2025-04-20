package glym.glym_spring.global.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCodeRefactor {

    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다", "Email Already Exists"),
    PHONENUMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 휴대폰 번호입니다", "PhoneNumber Already Exists"),


    //이메일 전송 오류
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패했습니다", "Email Send Failed"),

    //인증코드 오류
    EMAIL_CODE_MISMATCH(HttpStatus.FORBIDDEN, "인증코드가 일치하지 않습니다", "Email Verification Failed"),
    EMAIL_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이메일에 대한 인증코드가 존재하지 않습니다", "Email Verification Failed"),

    //토큰 오류
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 refresh token 입니다", "Invalid Refresh Token"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "refresh token 이 존재하지 않습니다", "Invalid Refresh Token"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "refresh token 이 만료되었습니다", "Invalid Refresh Token"),
    REFRESH_TOKEN_MISMATCH(HttpStatus.FORBIDDEN, "본인의 refresh token 이 아닙니다", "Invalid Refresh Token");


    private final HttpStatus status;
    private final String errorMessage;//한글 메시지
    private final String message;   //영어 메시지
}
