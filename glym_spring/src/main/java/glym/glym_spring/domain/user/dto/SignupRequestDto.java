package glym.glym_spring.domain.user.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class SignupRequestDto {

    @NotBlank(message = "이메일은 비어있을 수 없습니다")  //@NotNull 은 "" 빈문자열도 허용
    private final String email;
    @NotBlank(message = "비밀번호는 비어있을 수 없습니다")
    private final String password;
    @NotBlank(message = "이름은 비어있을 수 없습니다")
    private final String username;
    @NotBlank(message = "휴대폰은 비어있을 수 없습니다")
    private final String phone;
}
