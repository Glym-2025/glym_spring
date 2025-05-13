package glym.glym_spring.global.filter;

import com.amazonaws.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import glym.glym_spring.domain.auth.service.RefreshTokenService;
import glym.glym_spring.global.dto.ApiResponse;
import glym.glym_spring.global.exception.CustomException;
import glym.glym_spring.global.exception.errorcode.ErrorCode;
import glym.glym_spring.global.utils.JWTUtil;
import glym.glym_spring.domain.auth.dto.CustomUserDetails;
import glym.glym_spring.domain.auth.dto.LoginRequest;


import glym.glym_spring.domain.auth.dto.LoginResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RefreshTokenService refreshTokenService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {

            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
            //클라이언트 요청에서 username, password 추출
            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();

            //스프링 시큐리티에서 email과 password를 검증하기 위해서는 token에 담아야 함
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);

            //token에 담은 검증을 위한 AuthenticationManager로 전달
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            log.error("JSON 파싱 중 오류 발생");
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
    }

    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {

        //UserDetails
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        //AccessToken 발급
        String accessToken = jwtUtil.createAccessToken(customUserDetails);
        log.info("Access Token: {}", accessToken);

        //RefreshToken 발급
        String refreshToken = jwtUtil.createRefreshToken(customUserDetails);
        log.info("Refresh Token: {}", refreshToken);


        refreshTokenService.saveRefreshToken(
                customUserDetails.getUser(),
                refreshToken,
                LocalDateTime.now().plus(Duration.ofMillis(jwtUtil.getRefreshExpirationTime()))
        );


        //헤더에 AccessToken 추가
        response.addHeader("Authorization", "Bearer " + accessToken);

        /*
        // 쿠키에 refreshToken 추가
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true); // HttpOnly 설정
        //배포시 true 로 수정
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtUtil.getRefreshExpirationTime() / 1000)); // 쿠키 maxAge는 초 단위 이므로, 밀리초를 1000으로 나눔
        response.addCookie(cookie);
        */

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(jwtUtil.getRefreshExpirationTime() / 1000)
                .build();

        response.setHeader("Set-Cookie", cookie.toString());

        // 로그인에 성공하면 유저 정보 반환
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 반환할 유저 정보
        LoginResponse loginResponse = LoginResponse.builder()
                .email(customUserDetails.getUsername())
                .username(customUserDetails.getName())
                .build();

        ApiResponse<LoginResponse> apiResponse = new ApiResponse<>(loginResponse, "Login Success");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.setStatus(HttpServletResponse.SC_OK);
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        log.error("로그인 실패: {}", failed.getMessage());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(401);

        ApiResponse<String> apiResponse = new ApiResponse<>(failed.getMessage(), response.getStatus(), "Login Failed");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
