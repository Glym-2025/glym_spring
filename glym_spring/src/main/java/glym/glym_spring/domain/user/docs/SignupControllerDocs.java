package glym.glym_spring.domain.user.docs;

import glym.glym_spring.global.dto.ApiResponse;
import glym.glym_spring.domain.user.dto.SignupRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;

public interface SignupControllerDocs {
    @Operation(
            summary = "회원가입",
            description = """
        ## 사용자가 이메일, 비밀번호, 이름, 전화번호 등을 입력하여 회원가입을 진행합니다.

        ### 📥 요청 형식
        - JSON Body:
        ```json
        {
          "email": "example@example.com",
          "password": "1234",
          "username": "홍길동",
          "phone": "01012345678"
        }
        ```
        
        ### 📤 응답
        - 200 OK: 회원가입 성공 메시지 반환
        ```json
        {
          "message": "Signup Success",
          "status": 200,
          "data": {
            "username": "나현구",
            "email": "nahyunKoo09@gmail.com"
          }
        }
        ```

        - 400 Bad Request: 형식 오류 또는 누락 시 에러 메시지 반환
        ```json
        {
          "message": "Validation Failed",
          "status": 400,
          "data": [
            "이메일은 비어있을 수 없습니다"
          ]
        }
        ```
        - 여러 에러 시 : 누락 사항 리스트로 반환
        ```json
        {
          "message": "Validation Failed",
          "status": 400,
          "data": [
            "비밀번호는 비어있을 수 없습니다",
            "이메일은 비어있을 수 없습니다"
          ]
        }
        ```
        """
    )
    ResponseEntity<ApiResponse<Object>> signupProcess(SignupRequestDto requestDto);

    @Operation(
            summary = "이메일 중복 확인",
            description = """
        ## 사용자가 입력한 이메일의 중복 여부를 확인합니다.

        ### 📤 요청 형식
        - GET /signup/check-email?email=example@example.com

        ### 📤 응답
        - 200 OK: 사용 가능한 이메일인 경우<br></br>
        ```json
        {
          "message": "Email Available",
          "status": 200,
          "data": "angelcindy09@gmail.com"
        }
        ```
        
        - 409 : 이미 존재하는 이메일인 경우<br></br>
        ```json
        {
          "message": "Email Already Exists",
          "status": 409,
          "data": {
            "email": "glym2025@gmail.com",
            "errorCode": "EMAIL_ALREADY_EXISTS",
            "errorMessage": "이미 사용 중인 이메일입니다"
          }
        }
        ```
        """
    )
    ResponseEntity<ApiResponse<Object>> checkEmailDuplicate(String email);
}
