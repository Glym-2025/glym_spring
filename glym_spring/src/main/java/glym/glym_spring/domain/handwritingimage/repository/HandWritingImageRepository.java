package glym.glym_spring.domain.handwritingimage.repository;

import glym.glym_spring.domain.handwritingimage.domain.HandwritingImage;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HandWritingImageRepository extends JpaRepository <HandwritingImage,Long> {

    @Override
    Optional<HandwritingImage> findById(Long aLong);
}
