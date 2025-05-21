package glym.glym_spring.domain.font.repository;

import glym.glym_spring.domain.font.domain.FontCreation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FontCreationRepository extends JpaRepository <FontCreation,Long> {
    List<FontCreation> findByUserId(Long userId);
    Boolean existsByUserIdAndFontName(Long userId, String fontName);
    @Query("SELECT f.id FROM FontCreation f WHERE f.s3FontKey = :s3FontKey")
    Long findByS3FontKey(@Param("s3FontKey") String s3FontKey);


}
