package glym.glym_spring.login.controller;

import glym.glym_spring.domain.user.service.RefreshTokenService;
import glym.glym_spring.global.dto.ApiResponse;
import glym.glym_spring.global.utils.JWTUtil;
import glym.glym_spring.login.docs.AuthDocs;
import glym.glym_spring.login.dto.LoginRequest;
import glym.glym_spring.login.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController implements AuthDocs {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ApiResponse<String>> refresh(@CookieValue("refreshToken") String refreshToken) {
        return refreshTokenService.validateRefreshToken(refreshToken);
    }
}
