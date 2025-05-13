package glym.glym_spring.domain.auth.service;

import glym.glym_spring.domain.auth.domain.RefreshToken;
import glym.glym_spring.domain.user.domain.User;
import glym.glym_spring.domain.auth.repository.RefreshTokenRepository;
import glym.glym_spring.global.dto.ApiResponse;
import glym.glym_spring.global.exception.CustomException;

import glym.glym_spring.global.exception.errorcode.ErrorCode;
import glym.glym_spring.global.utils.JWTUtil;
import glym.glym_spring.domain.auth.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTUtil jwtUtil;

    public void saveRefreshToken(User user, String token, LocalDateTime expiresAt) {
        RefreshToken rt = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiresAt(expiresAt)
                .build();

        refreshTokenRepository.save(rt);

        log.info("Refresh token saved");
    }


    public ResponseEntity<ApiResponse<String>> refreshAccessToken(String oldRefreshToken) {
        RefreshToken rt = validateRefreshToken(oldRefreshToken);
        User user = rt.getUser();

        // 새 AccessToken 발급
        String newAccessToken = jwtUtil.createAccessToken(new CustomUserDetails(user));

        //이전 refresh token 블랙리스트 처리
        refreshTokenRepository.deleteById(rt.getId());

        // 새 RefreshToken 발급
        String newRefreshToken = jwtUtil.createRefreshToken(new CustomUserDetails(user));

        //새 RefreshToken 저장
        saveRefreshToken(
                user,
                newRefreshToken,
                jwtUtil.getRefreshExpiryDate(newRefreshToken));

        ResponseCookie rtCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofMillis(jwtUtil.getRefreshExpirationTime()))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken)
                .header(HttpHeaders.SET_COOKIE, rtCookie.toString())
                .body(new ApiResponse<>(null, "AccessToken Refresh Success"));
    }


    @Transactional
    public ResponseEntity<ApiResponse<String>> deleteRefreshToken(CustomUserDetails customUserDetails, String oldRefreshToken) {
        RefreshToken rt = validateRefreshToken(oldRefreshToken);
        User user = rt.getUser();

        // 사용자 본인 검증
        log.info("custom: " + customUserDetails.getUser().getId());
        log.info("user: " + user.getId());
        if (!user.getId().equals(customUserDetails.getUser().getId())) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_MISMATCH);
        }

        refreshTokenRepository.deleteByUser(user);

        return ResponseEntity.ok()
                .body(new ApiResponse<>(customUserDetails.getUsername(), "Logout Success"));
    }

        private RefreshToken validateRefreshToken(String refreshToken) {
            // 토큰 유효성 검사
            if (!jwtUtil.validateToken(refreshToken)) {
                throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
            }

            Optional<RefreshToken> optionalToken = refreshTokenRepository.findByToken(refreshToken);

            //토큰 db 에 존재하는지 검사
            RefreshToken savedToken = optionalToken
                    .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

            // 만료 여부 검증
            if (savedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
            }

            return savedToken;
        }
}
