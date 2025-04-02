package glym.glym_spring.domain.font.repository;

import glym.glym_spring.domain.font.domain.HandwritingImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HandWritingImageRepository extends JpaRepository <HandwritingImage,Long> {
}
