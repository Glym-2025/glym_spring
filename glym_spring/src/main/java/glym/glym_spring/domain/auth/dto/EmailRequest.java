package glym.glym_spring.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EmailRequest {

    @Email(message = "유효하지 않은 이메일 형식입니다")
    @NotBlank(message = "이메일은 비어있을 수 없습니다")
    private String to;
}
