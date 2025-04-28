package glym.glym_spring.domain.font.repository;

import glym.glym_spring.domain.font.domain.FontProcessingJob;
import glym.glym_spring.domain.font.domain.JobStatus;
import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface FontProcessingJobRepository extends R2dbcRepository<FontProcessingJob, String> {


    @Override
    Mono<FontProcessingJob> findById(String s);

    List<FontProcessingJob> findByStatusInAndCreatedAtBefore(Collection<JobStatus> statuses, LocalDateTime createdAtBefore);
}