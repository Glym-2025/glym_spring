package glym.glym_spring.global.exception;

import glym.glym_spring.global.exception.errorcode.ErrorCodeRefactor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class EmailErrorResponseRefactor {
    private String email;
    private ErrorCodeRefactor errorCode;
    private String errorMessage;
}
