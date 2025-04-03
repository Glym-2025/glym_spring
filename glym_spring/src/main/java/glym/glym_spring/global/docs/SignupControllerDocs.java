package glym.glym_spring.global.docs;

import glym.glym_spring.domain.user.dto.SignupRequestDto;
import glym.glym_spring.domain.user.dto.SignupResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;

public interface SignupControllerDocs {
    @Operation(
            summary = "회원가입",
            description = """
        ## 사용자가 이메일, 비밀번호, 닉네임 등을 입력하여 회원가입을 진행합니다.

        **요청 형식**
        - JSON Body: <br></br>
            {<br></br>
              "email": "example@example.com", <br></br>
              "password": "1234",<br></br>
              "username": "홍길동",<br></br>
              "phone": "01012345678"<br></br>
            }

        **응답**
        - 200 OK: 회원가입 성공 메시지 반환<br></br>
                    {<br></br>
                      "message": "User Signup successful",<br></br>
                      "username": "구나현"<br></br>
                      "email": "example@example.com", <br></br>
                    }               
        - 400 Bad Request: 형식 오류 또는 누락 시 에러 메시지 반환
        """
    )
    ResponseEntity<SignupResponseDto> signupProcess(SignupRequestDto requestDto);

    @Operation(
            summary = "이메일 중복 확인",
            description = """
        ## 사용자가 입력한 이메일의 중복 여부를 확인합니다.

        **요청 형식**
        - GET /signup/check-email?email=example@example.com

        **응답**
        - 200 OK: 사용 가능한 이메일인 경우<br></br>
            {<br></br>
              "message": "User email available",<br></br>
              "username": null, <br></br>
              "email": "example@example.com"<br></br>
            }

        - 409 : 이미 존재하는 이메일인 경우<br></br>
                {<br></br>
                   "errorCode": "EMAIL_ALREADY_EXISTS",<br></br>
                   "errorMessage": "이미 사용 중인 이메일입니다"<br></br>
                }
        """
    )
    ResponseEntity<SignupResponseDto> checkEmailDuplicate(String email);
}
