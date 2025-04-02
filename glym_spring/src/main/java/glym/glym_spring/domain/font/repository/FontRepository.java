package glym.glym_spring.domain.font.repository;

import glym.glym_spring.domain.font.domain.Font;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FontRepository extends JpaRepository <Font,Long> {
}
