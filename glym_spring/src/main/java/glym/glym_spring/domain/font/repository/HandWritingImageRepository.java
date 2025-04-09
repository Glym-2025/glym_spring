package glym.glym_spring.domain.font.repository;

import glym.glym_spring.domain.font.domain.HandwritingImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HandWritingImageRepository extends JpaRepository <HandwritingImage,Long> {

    @Override
    Optional<HandwritingImage> findById(Long aLong);
}
