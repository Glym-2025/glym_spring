package glym.glym_spring.domain.font.service;


import glym.glym_spring.domain.font.domain.FontProcessingJob;
import glym.glym_spring.domain.font.dto.AIRequestDto;
import glym.glym_spring.domain.font.dto.FontCreateRequest;
import glym.glym_spring.domain.font.dto.JobStatusResponseDto;
import glym.glym_spring.domain.font.repository.FontCreationRepository;
import glym.glym_spring.domain.font.repository.FontProcessingJobRepository;
import glym.glym_spring.domain.font.utils.ImageConverter;
import glym.glym_spring.domain.font.validator.HandWritingImageValidator;
import glym.glym_spring.global.infrastructure.storage.StorageService;
import glym.glym_spring.domain.user.domain.User;
import glym.glym_spring.domain.user.repository.UserRepository;
import glym.glym_spring.global.exception.domain.CustomException;
import glym.glym_spring.global.exception.domain.ImageValidationException;
import glym.glym_spring.global.infrastructure.client.FontProcessingClient;
import glym.glym_spring.global.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;

import static glym.glym_spring.domain.font.domain.JobStatus.PROCESSING;
import static glym.glym_spring.global.exception.errorcode.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FontService {
    private final HandWritingImageValidator handWritingImageValidator;
    private final StorageService StorageService;
    private final FontProcessingClient fontProcessingClient;
    private final FontProcessingJobRepository fontProcessingJobRepository;
    private final UserRepository userRepository;
    private final S3Presigner s3Presigner;


    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    public String createFont(FontCreateRequest request) throws IOException, ImageValidationException {


        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));


        String uuid = UUID.randomUUID().toString();

        MultipartFile handWritingImage = request.getHandWritingImage();
        String fontName = request.getFontName();

        String s3Key = processingImage(handWritingImage,userId,uuid);

        FontProcessingJob job = FontProcessingJob.builder()
                .status(PROCESSING)
                .user(user)
                .fontName(fontName)
                .s3ImageKey(s3Key)
                .jobId(uuid)
                .build();

        fontProcessingJobRepository.save(job);

        fontProcessingClient.sendProcessingRequest(
                AIRequestDto.builder()
                        .jobId(uuid)
                        .userId(userId)
                        .fontName(fontName)
                        .s3ImageKey(s3Key)
                        .callbackUrl("http://localhost:8080/api/font/callback")
                        .build()
        );

        return uuid;
    }

    public String processingImage (MultipartFile handWritingImage,  Long userId, String uuid) throws IOException, ImageValidationException {
        //handWritingImageValidator.validate(handWritingImage);
        ImageConverter.convertToPng(handWritingImage);

        return StorageService.storeImage(handWritingImage, uuid,userId);

    }

    public Iterable<JobStatusResponseDto> getJobStatusIterable(String jobId) {
        return new Iterable<JobStatusResponseDto>() {
            @Override
            public Iterator<JobStatusResponseDto> iterator() {
                return new Iterator<JobStatusResponseDto>() {
                    private boolean running = true;
                    private FontProcessingJob job;

                    @Override
                    public boolean hasNext() {
                        if (!running) return false;
                        job = fontProcessingJobRepository.findById(jobId).orElse(null);
                        if (job == null) return false;
                        return !"COMPLETED".equals(job.getStatus()) && job.getErrorMessage() == null;
                    }

                    @Override
                    public JobStatusResponseDto next() {
                        if (!hasNext()) throw new NoSuchElementException();
                        try {
                            Thread.sleep(1000); // 1초 대기
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        String status = job.getStatus().toString().toUpperCase();
                        String fontUrl = null;
                        String errorMessage = null;

                        if ("COMPLETED".equals(status) && job.getS3FontKey() != null) {
                            fontUrl = generatePresignedUrl(job.getS3FontKey());
                            System.out.println("good job");
                            running = false; // 완료 시 종료
                        } else if ("FAILED".equals(status)) {
                            errorMessage = job.getErrorMessage();
                            running = false; // 실패 시 종료
                        }

                        return new JobStatusResponseDto(status, fontUrl, errorMessage);
                    }
                };
            }
        };
    }

    private String generatePresignedUrl(String objectKey) {
        // objectKey에서 s3://버킷명/ 부분 제거
        final String processedKey;
        if (objectKey.startsWith("s3://")) {
            int pathStartIndex = objectKey.indexOf("/", 5);
            if (pathStartIndex != -1) {
                processedKey = objectKey.substring(pathStartIndex + 1);
            } else {
                processedKey = objectKey; // 기본값 설정
            }
        } else {
            processedKey = objectKey;
        }

        var presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(b -> b.bucket(bucketName).key(processedKey))
                .build();
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

}
