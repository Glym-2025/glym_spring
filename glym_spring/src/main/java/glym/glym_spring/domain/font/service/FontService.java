package glym.glym_spring.domain.font.service;

import glym.glym_spring.domain.aiserverclient.domain.AIServerClient;
import glym.glym_spring.domain.font.domain.FontProcessingJob;
import glym.glym_spring.domain.font.domain.JobStatus;
import glym.glym_spring.domain.font.dto.FontCreateRequest;
import glym.glym_spring.domain.font.dto.JobStatusResponseDto;
import glym.glym_spring.domain.font.repository.FontCreationRepository;
import glym.glym_spring.domain.font.repository.FontProcessingJobRepository;
import glym.glym_spring.domain.font.utils.ImageConverter;
import glym.glym_spring.domain.font.validator.HandWritingImageValidator;
import glym.glym_spring.domain.s3stroage.service.S3StorageService;
import glym.glym_spring.global.exception.domain.ImageValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


import static glym.glym_spring.domain.font.domain.JobStatus.PROCESSING;

@Service
@RequiredArgsConstructor
@Slf4j
public class FontService {
    private final FontCreationRepository fontCreationRepository;
    private final HandWritingImageValidator handWritingImageValidator;
    private final S3StorageService s3StorageService;
    private final AIServerClient aiServerClient;
    private final FontProcessingJobRepository fontProcessingJobRepository;
    private final S3Presigner s3Presigner;

    public String createFont(FontCreateRequest request) throws IOException, ImageValidationException {


        Long userId=1L;
        // 2. 현재 사용자 ID 가져오기 (Spring Security 사용)
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//        Long userId = userDetails.getUserId();

        String uuid = UUID.randomUUID().toString();


        MultipartFile handWritingImage = request.getHandWritingImage();
        String fontName = request.getFontName();


        String s3Key = processingImage(handWritingImage,userId,uuid);


        FontProcessingJob job = FontProcessingJob.builder()
                .status(PROCESSING)
                .userId(userId)
                .fontName(fontName)
                .s3ImageKey(s3Key)
                .jobId(uuid)
                .build();

        fontProcessingJobRepository.save(job);


        aiServerClient.sendToAIServer(job);

        // 작업요청 을 생성 유저 아이디, s3key (이미지경로), 작업아이디, 스테이터스, 생성시간
        return uuid;
    }

    public String processingImage (MultipartFile handWritingImage,  Long userId, String uuid) throws IOException, ImageValidationException {
        //handWritingImageValidator.validate(handWritingImage);

        ImageConverter.convertToPng(handWritingImage);

        return s3StorageService.storeImage(handWritingImage, uuid,userId);

    }

    public Flux<JobStatusResponseDto> getJobStatusStream(String jobId) {
        return Flux.interval(Duration.ofSeconds(1))
                .flatMap(i -> fontProcessingJobRepository.findById(jobId)
                        .map(this::mapToResponseDto)
                        .switchIfEmpty(Mono.error(new RuntimeException("Job not found")))
                )
                .takeUntil(dto -> dto.getStatus().equals("COMPLETED") || dto.getStatus().equals("FAILED"))
                .timeout(Duration.ofMinutes(30));
    }

    private JobStatusResponseDto mapToResponseDto(FontProcessingJob job) {
        JobStatusResponseDto dto = new JobStatusResponseDto();
        dto.setStatus(job.getStatus().name());

        if (job.getStatus() == JobStatus.COMPLETED && job.getS3FontKey() != null) {
            String presignedUrl = generatePresignedUrl(job.getS3FontKey());
            dto.setFontUrl(presignedUrl);
        }
        if (job.getStatus() == JobStatus.FAILED && job.getErrorMessage() != null) {
            dto.setErrorMessage(job.getErrorMessage());
        }

        return dto;
    }

    private String generatePresignedUrl(String s3FontKey) {

        var presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(1))
                .getObjectRequest(gor -> gor.bucket("your-bucket").key(s3FontKey))
                .build();
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    // 작업 타임아웃 처리 (예: 30분 후 FAILED)
    public void checkJobTimeout() {
        fontProcessingJobRepository.findByStatusInAndCreatedAtBefore(
                List.of(JobStatus.PENDING, JobStatus.PROCESSING),
                LocalDateTime.now().minusMinutes(30)
        ).forEach(job -> {
            job.setStatus(JobStatus.FAILED);
            job.setErrorMessage("Job timed out");
            fontProcessingJobRepository.save(job);
        });
    }


}
