package glym.glym_spring.domain.auth.service;

import glym.glym_spring.domain.auth.domain.RefreshToken;
import glym.glym_spring.domain.auth.repository.RefreshTokenRepository;
import glym.glym_spring.domain.user.domain.User;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private RefreshToken expiredToken;


    @BeforeEach
    void setUp() {
        expiredToken = new RefreshToken()
                .builder()
                .id(1L)
                .expiresAt(LocalDateTime.now().minusHours(1))
                .token("expired-token")
                .createdAt(LocalDateTime.now().minusHours(2))
                .build();// 이미 만료됨
    }

    @Test
    void testDeleteExpiredTokensLogsCorrectly() {
        // when
        refreshTokenService.deleteExpiredRefreshTokens();

        // then
        verify(refreshTokenRepository, times(1)).deleteByExpiresAtBefore(any());
    }
}
