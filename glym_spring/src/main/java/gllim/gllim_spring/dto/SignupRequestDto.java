package gllim.gllim_spring.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class SignupRequestDto {

    @NotBlank   //@NotNull 은 "" 빈문자열도 허용
    private final String email;
    @NotBlank
    private final String password;
    @NotBlank
    private final String username;

}
