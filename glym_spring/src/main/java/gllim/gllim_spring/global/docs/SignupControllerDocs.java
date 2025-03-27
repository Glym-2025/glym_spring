package gllim.gllim_spring.global.docs;

import gllim.gllim_spring.domain.user.dto.SignupRequestDto;
import gllim.gllim_spring.domain.user.dto.SignupResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;

public interface SignupControllerDocs {
    @Operation(
            summary = "회원가입",
            description = """
        사용자가 이메일, 비밀번호, 닉네임 등을 입력하여 회원가입을 진행합니다.

        **요청 형식**
        - JSON Body:
            {
              "email": "example@example.com",
              "password": "1234",
              "username": "홍길동",
              "phone": "01012345678"
            }

        **응답**
        - 200 OK: 회원가입 성공 메시지 반환
                    {
                      "message": "User Signup successful",
                      "username": "구나현"
                    }                   
        - 409 : 이미 존재하는 이메일인 경우
                    {
                      "errorCode": "EMAIL_ALREADY_EXISTS",
                      "errorMessage": "이미 사용 중인 이메일입니다"
                    }
        - 400 Bad Request: 형식 오류 또는 누락 시 에러 메시지 반환
        """
    )
    ResponseEntity<SignupResponseDto> signupProcess(SignupRequestDto requestDto);
}
