package glym.glym_spring.auth.repository;

import glym.glym_spring.auth.domain.RefreshToken;
import glym.glym_spring.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    void deleteByUser(User user);
    Optional<RefreshToken> findByToken(String token);
}
