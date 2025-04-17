package glym.glym_spring.domain.font.repository;

import glym.glym_spring.domain.font.domain.FontProcessingJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FontProcessingJobRepository extends JpaRepository<FontProcessingJob, String> {
    @Override
    Optional<FontProcessingJob> findById(String jobId);
}