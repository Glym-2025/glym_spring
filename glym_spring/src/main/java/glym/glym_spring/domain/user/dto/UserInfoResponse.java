package glym.glym_spring.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserInfoResponse {
    private String email;
    private String username;
    private String phone;
    private String profileImageURL;
}
