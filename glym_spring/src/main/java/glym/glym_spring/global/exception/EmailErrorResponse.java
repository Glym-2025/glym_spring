package glym.glym_spring.global.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class EmailErrorResponse {
    private String email;
    private ErrorCode errorCode;
    private String errorMessage;
}
