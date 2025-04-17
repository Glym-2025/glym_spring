package glym.glym_spring.domain.user.service;

import glym.glym_spring.domain.user.domain.RefreshToken;
import glym.glym_spring.domain.user.domain.User;
import glym.glym_spring.domain.user.repository.RefreshTokenRepository;
import glym.glym_spring.global.dto.ApiResponse;
import glym.glym_spring.global.exception.CustomException;

import glym.glym_spring.global.exception.errorcode.ErrorCode;
import glym.glym_spring.global.utils.JWTUtil;
import glym.glym_spring.login.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTUtil jwtUtil;

    public void saveRefreshToken(User user, String token, LocalDateTime expiresAt) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiresAt(expiresAt)
                .build();

        refreshTokenRepository.save(refreshToken);

        log.info("Refresh token saved");
    }

    public ResponseEntity<ApiResponse<String>> validateRefreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        log.info("요청한 RefreshToken: {}", refreshToken);
        Optional<RefreshToken> optionalToken = refreshTokenRepository.findByToken(refreshToken);
        log.info("조회된 RefreshToken: {}", optionalToken);
        RefreshToken savedToken = optionalToken
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        // 유효성 검증
        if (savedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        // 새 AccessToken 발급
        String newAccessToken = jwtUtil.createAccessToken(new CustomUserDetails(savedToken.getUser()));

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + newAccessToken)
                .body(new ApiResponse<>(null, "AccessToken Refresh Success"));
    }
}
