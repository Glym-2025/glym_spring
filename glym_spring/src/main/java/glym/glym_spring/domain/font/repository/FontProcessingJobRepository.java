package glym.glym_spring.domain.font.repository;

import glym.glym_spring.domain.font.domain.FontProcessingJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FontProcessingJobRepository extends JpaRepository<FontProcessingJob, Long> {
    @Override
    Optional<FontProcessingJob> findById(Long aLong);
}
