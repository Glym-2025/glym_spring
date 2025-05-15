package glym.glym_spring.domain.font.service;

import glym.glym_spring.domain.font.domain.FontCreation;
import glym.glym_spring.domain.font.domain.FontProcessingJob;
import glym.glym_spring.domain.font.domain.JobStatus;
import glym.glym_spring.domain.font.dto.AIRequestDto;
import glym.glym_spring.domain.font.dto.AIResultDto;
import glym.glym_spring.domain.font.repository.FontCreationRepository;
import glym.glym_spring.domain.font.repository.FontProcessingJobRepository;
import glym.glym_spring.domain.user.domain.User;
import glym.glym_spring.domain.user.repository.UserRepository;
import glym.glym_spring.global.exception.domain.CustomException;
import glym.glym_spring.global.infrastructure.client.FontProcessingClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static glym.glym_spring.global.exception.errorcode.ErrorCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class FontCallbackService {

    private final FontProcessingJobRepository fontProcessingJobRepository;
    private final FontCreationRepository fontCreationRepository;
    private final FontProcessingClient fontProcessingClient;
    private final UserRepository userRepository;

    @Value("${backend-server.callback-url}")
    private String callbackUrl;

    @Transactional
    public void initiateFontProcessing(FontProcessingJob job) {
        AIRequestDto request = AIRequestDto.builder()
                .jobId(job.getJobId())
                .userId(job.getUser().getId())
                .fontName(job.getFontName())
                .s3ImageKey(job.getS3ImageKey())
                .callbackUrl(callbackUrl)
                .build();

        fontProcessingClient.sendProcessingRequest(request);
        log.info("Font processing request initiated. jobId: {}", job.getJobId());
    }

    @Transactional
    public void processCallback(AIResultDto result) {
        String jobId = result.getJobId();
        FontProcessingJob job = fontProcessingJobRepository.findById(jobId)
                .orElseThrow(() -> {
                    log.error("Job not found for jobId: {}", jobId);
                    return new CustomException(JOB_NOT_FOUND);
                });
        System.out.println("result = " + result);

        job.updateStatus(JobStatus.fromString(result.getStatus()));
        job.setS3FontKey(result.getS3FontPath());
        fontProcessingJobRepository.save(job);

        // Optional<User> byId = userRepository.findById(job.getUser().getId());
        User user = userRepository.findById(job.getUser().getId())
                .orElseThrow(() -> {
                    log.error("사용자를 찾을 수 없습니다. userId: {}", job.getUser().getId());
                    return new CustomException(USER_NOT_FOUND);
                });

        // 사용자의 폰트 카운트 증가
        user.incrementFontCount();
        userRepository.save(user);

        FontCreation fontCreation = FontCreation.builder()
                .fontName(job.getFontName())
                .user(job.getUser())
                .fontDescription(job.getFontDescription())
                .s3FontKey(result.getS3FontPath())
                .s3ImageKey(job.getS3ImageKey())
                .build();

        fontCreationRepository.save(fontCreation);
        log.info("Callback processed for jobId: {}", jobId);
    }
}