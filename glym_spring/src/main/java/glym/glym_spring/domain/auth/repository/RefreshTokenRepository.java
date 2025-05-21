package glym.glym_spring.domain.auth.repository;

import glym.glym_spring.domain.auth.domain.RefreshToken;
import glym.glym_spring.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    void deleteByUser(User user);

    Optional<RefreshToken> findByToken(String token);


    @Modifying
    void deleteByExpiresAtBefore(LocalDateTime now);

}
