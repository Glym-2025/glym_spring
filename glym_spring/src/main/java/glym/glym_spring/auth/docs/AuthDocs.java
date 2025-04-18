package glym.glym_spring.auth.docs;

import glym.glym_spring.auth.dto.CustomUserDetails;
import glym.glym_spring.global.dto.ApiResponse;
import glym.glym_spring.auth.dto.LoginRequest;
import glym.glym_spring.auth.dto.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;

public interface AuthDocs {

    @Operation(
            summary = "로그인",
            description = """
                ## 사용자가 이메일, 비밀번호를 입력해 로그인을 진행합니다.

                ### 📥 요청 형식
                - JSON Body:
                ```json
                {
                  "email": "example@example.com",
                  "password": "1234"
                }
                ```

                ### 📤 응답
                - 200 OK: 로그인 성공 메시지 반환
                - Access Token 은 헤더에, Refresh Token 은 쿠키에 저장됨 (※ Swagger 에서는 쿠키는 보이지 않음)
                ```json
                {
                  "message": "Login Success",
                  "status": 200,
                  "data": {
                    "email": "nahyunKoo09@gmail.com",
                    "username": "구나현"
                  }
                }
                ```
                ```yaml
                authorization: Bearer (토큰)
                cache-control: no-cache,no-store,max-age=0,must-revalidate
                connection: keep-alive
                content-length: 105
                content-type: application/json;charset=UTF-8
                date: Sun, 13 Apr 2025 13:36:13 GMT
                expires: 0
                keep-alive: timeout=60
                pragma: no-cache
                vary: Origin,Access-Control-Request-Method,Access-Control-Request-Headers
                x-content-type-options: nosniff
                x-frame-options: DENY
                x-xss-protection: 0
                ```

                - 401 Unauthorized: 비밀번호가 일치하지 않을 경우 에러 메시지 출력
                ```json
                {
                  "message": "Login Failed",
                  "status": 401,
                  "data": "자격 증명에 실패하였습니다."
                }
                ```
                """
    )
    ResponseEntity<LoginResponse> login(LoginRequest loginRequest);

    @Operation(
            summary = "refresh token 으로 access token 새로 발급받기",
            description = """
                ## refresh token 이 만료되면, 해당 refresh token 으로 새로 발급받습니다
               
                ## ⚠️ Swagger 사용 시 주의사항
                - **Swagger UI는 브라우저 보안 제한으로 Cookie 기반 인증 테스트가 정상적으로 동작하지 않습니다.**
                - 따라서 **Postman 또는 실제 클라이언트 환경에서 테스트해 주세요.**

                ### 📥 요청 형식
                - 요청 시 별도의 Body 는 필요하지 않습니다.
                - **Refresh Token** 은 쿠키로 전송합니다.

                ### 📤 응답
                - 200 OK: 토큰 재발급 성공
                - Access Token 은 헤더에 저장됨
                - Refresh Token 탈취 예방을 위해 새로운 Refresh Token 도 쿠키로 발급됨
                ```json
                {
                  "message": "AccessToken Refresh Success",
                  "status": 200,
                  "data": null
                }
                ```

                - 401 Unauthorized: 유효하지 않은 refresh token 일 경우 에러 메시지 출력
                ```json
                {
                  "message": "Invalid Refresh Token",
                  "status": 401,
                  "data": {
                    "errorCode": "INVALID_REFRESH_TOKEN",
                    "errorMessage": "유효하지 않은 refresh token 입니다"
                  }
                }
                ```
                
                - 401 Unauthorized: 존재하지 않는 refresh token 일 경우 에러 메시지 출력
                ```json
                {
                  "message": "Invalid Refresh Token",
                  "status": 401,
                  "data": {
                    "errorCode": "REFRESH_TOKEN_NOT_FOUND",
                    "errorMessage": "refresh token 이 존재하지 않습니다"
                  }
                }
                ```
                """
    )
    ResponseEntity<ApiResponse<String>> refresh(String refreshToken);

    @Operation(
            summary = "로그아웃",
            description = """
                ## 사용자가 access token 과 refresh token 으로 로그아웃을 진행합니다
                
                ## ⚠️ Swagger 사용 시 주의사항
                - **Swagger UI는 브라우저 보안 제한으로 Cookie 기반 인증 테스트가 정상적으로 동작하지 않습니다.**
                - 따라서 **Postman 또는 실제 클라이언트 환경에서 테스트해 주세요.**

                ### 📥 요청 형식
                - 요청 시 별도의 Body 는 필요하지 않습니다.
                - **Access Token** 은 헤더로 전송합니다.
                - **Refresh Token** 은 쿠키로 전송합니다.

                ### 📤 응답
                - 200 OK: 로그아웃 성공
                - Refresh Token 이 DB 에서 삭제됨
                ```json
                {
                  "message": "Logout Success",
                  "status": 200,
                  "data": "로그아웃 한 사용자 이름"
                }
                ```

                - 401 Unauthorized: 유효하지 않은 refresh token 일 경우 에러 메시지 출력
                ```json
                {
                  "message": "Invalid Refresh Token",
                  "status": 401,
                  "data": {
                    "email": null,
                    "errorCode": "INVALID_REFRESH_TOKEN",
                    "errorMessage": "유효하지 않은 refresh token 입니다"
                  }
                }
                ```
                
                - 401 Unauthorized: 존재하지 않는 refresh token 일 경우 에러 메시지 출력
                ```json
                {
                  "message": "Invalid Refresh Token",
                  "status": 401,
                  "data": {
                    "errorCode": "REFRESH_TOKEN_NOT_FOUND",
                    "errorMessage": "refresh token 이 존재하지 않습니다"
                  }
                }
                ```
                
                - 401 Unauthorized: 만료된 refresh token 일 경우 에러 메시지 출력
                ```json
                {
                  "message": "Invalid Refresh Token",
                  "status": 401,
                  "data": {
                    "errorCode": "REFRESH_TOKEN_EXPIRED",
                    "errorMessage": "refresh token 이 만료되었습니다"
                  }
                }
                ```
                
                - 401 Unauthorized: 사용자 본인의 refresh token 이 아닐 경우 에러 메시지 출력
                ```json
                {
                  "message": "Invalid Refresh Token",
                  "status": 401,
                  "data": {
                    "errorCode": "REFRESH_TOKEN_MISMATCH",
                    "errorMessage": "본인의 refresh token 이 아닙니다"
                  }
                }
                """
    )
    ResponseEntity<ApiResponse<String>> logout(CustomUserDetails customUserDetails, String refreshToken);
}
