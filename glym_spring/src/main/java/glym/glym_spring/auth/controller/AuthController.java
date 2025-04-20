package glym.glym_spring.auth.controller;

import glym.glym_spring.auth.dto.CustomUserDetails;
import glym.glym_spring.auth.dto.EmailRequest;
import glym.glym_spring.auth.service.EmailService;
import glym.glym_spring.auth.service.RefreshTokenService;
import glym.glym_spring.global.dto.ApiResponse;
import glym.glym_spring.global.utils.JWTUtil;
import glym.glym_spring.auth.docs.AuthDocs;
import glym.glym_spring.auth.dto.LoginRequest;
import glym.glym_spring.auth.dto.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController implements AuthDocs {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<String>> refresh(@CookieValue("refreshToken") String refreshToken) {
        return refreshTokenService.refreshAccessToken(refreshToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @CookieValue("refreshToken") String refreshToken
    ) {
        return refreshTokenService.deleteRefreshToken(customUserDetails, refreshToken);
    }

    @PostMapping("/send-email")
    public ResponseEntity<ApiResponse<String>> sendEmail(@RequestBody @Valid EmailRequest emailRequest) throws Exception {
        emailService.sendEmail(emailRequest.getTo());
        return ResponseEntity.ok(ApiResponse.success(emailRequest.getTo(), "Email Send Success"));
    }

}
