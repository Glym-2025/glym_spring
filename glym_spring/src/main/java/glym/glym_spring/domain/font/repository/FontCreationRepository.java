package glym.glym_spring.domain.font.repository;

import glym.glym_spring.domain.font.domain.FontCreation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FontCreationRepository extends JpaRepository <FontCreation,Long> {
}
