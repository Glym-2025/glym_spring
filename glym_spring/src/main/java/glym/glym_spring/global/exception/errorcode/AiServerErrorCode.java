package glym.glym_spring.global.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AiServerErrorCode {

    AI_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"AI 서버 응답 오류: "),
    AI_CONNECTION_ERROR(HttpStatus.REQUEST_TIMEOUT,"AI 서버 연결 중 오류가 발생했습니다: ");

    private final HttpStatus status;
    private final String message;
}
