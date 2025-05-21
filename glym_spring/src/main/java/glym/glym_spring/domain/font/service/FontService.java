package glym.glym_spring.domain.font.service;

import glym.glym_spring.domain.font.domain.FontCreation;
import glym.glym_spring.domain.font.domain.FontProcessingJob;
import glym.glym_spring.domain.font.dto.*;
import glym.glym_spring.domain.font.repository.FontCreationRepository;
import glym.glym_spring.domain.font.repository.FontProcessingJobRepository;
import glym.glym_spring.domain.font.utils.ImageConverter;
import glym.glym_spring.domain.font.validator.FontValidator;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import static glym.glym_spring.domain.font.domain.JobStatus.PROCESSING;
import static glym.glym_spring.global.exception.errorcode.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FontService {
    private final HandWritingImageValidator handWritingImageValidator;
    private final FontCreationRepository fontCreationRepository;
    private final StorageService storageService;
    private final FontProcessingClient fontProcessingClient;
    private final FontProcessingJobRepository fontProcessingJobRepository;
    private final UserRepository userRepository;
    private final FontValidator fontValidator;

    @Value("${backend-server.callback-url}")
    private String callbackUrl;


    public String createFont(Long userId, FontCreateRequest request) throws IOException, ImageValidationException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

// 폰트 갯수 및 이름 검증 로직 호출
        fontValidator.validateFontCreationLimit(user);
        fontValidator.validateFontNameDuplicate(userId, request.getFontName());

        String uuid = UUID.randomUUID().toString();
        String fontName = request.getFontName();
        MultipartFile handWritingImage = request.getHandWritingImage();
        String fontDescription = request.getFontDescription();

        String s3Key = processingImage(handWritingImage,userId,uuid);

        FontProcessingJob job = FontProcessingJob.builder()
                .status(PROCESSING)
                .user(user)
                .fontName(fontName)
                .fontDescription(fontDescription)
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
                        .callbackUrl(callbackUrl)
                        .build()
        );

        return uuid;
    }

    public String processingImage (MultipartFile handWritingImage,  Long userId, String uuid) throws IOException, ImageValidationException {
        //handWritingImageValidator.validate(handWritingImage);
        ImageConverter.convertToPng(handWritingImage);

        return storageService.storeImage(handWritingImage, uuid,userId);
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
                        Long fontId= null;
                        String errorMessage = null;

                        if ("COMPLETED".equals(status) && job.getS3FontKey() != null) {

                            fontId=fontCreationRepository.findByS3FontKey(job.getS3FontKey());


                            running = false; // 완료 시 종료
                        } else if ("FAILED".equals(status)) {
                            errorMessage = job.getErrorMessage();
                            running = false; // 실패 시 종료
                        }

                        return new JobStatusResponseDto(status, fontUrl, errorMessage,fontId);
                    }
                };
            }
        };
    }

    @Transactional(readOnly = true)
    public List<FontListResponseDto> getUserFonts(Long userId) {

        List<FontCreation> fontCreations = fontCreationRepository.findByUserId(userId);

        return fontCreations.stream()
                .map(font -> FontListResponseDto.builder()
                        .id(font.getId())
                        .fontName(font.getFontName())
                        .createdAt(font.getCreatedAt())
                        .fontDescription(font.getFontDescription())
                        .fontUrl(storageService.generatePresignedUrl(font.getS3FontKey()))
                        .build())
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<FontDownloadResponseDto> getDownloadUrlsForFonts(Long userId, List<Long> fontIds) {

        // 사용자가 소유한 폰트만 필터링하여 가져오기
        List<FontCreation> fonts = fontCreationRepository.findAllById(fontIds)
                .stream()
                .filter(font -> font.getUser().getId().equals(userId))
                .collect(Collectors.toList());

        if (fonts.isEmpty()) {
            throw new CustomException(FONT_NOT_FOUND);
        }

        return fonts.stream()
                .map(font -> FontDownloadResponseDto.builder()
                        .id(font.getId())
                        .fontName(font.getFontName())
                        .downloadUrl(storageService.generatePresignedUrl(font.getS3FontKey()))
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteFonts(Long userId, List<Long> fontIds) {

        // 사용자가 소유한 폰트만 필터링하여 가져오기
        List<FontCreation> fonts = fontCreationRepository.findAllById(fontIds)
                .stream()
                .filter(font -> font.getUser().getId().equals(userId))
                .collect(Collectors.toList());

        if (fonts.isEmpty()) {
            throw new CustomException(FONT_NOT_FOUND);
        }

        // 폰트 삭제
        fontCreationRepository.deleteAll(fonts);



        // 사용자의 폰트 카운트 업데이트

    }
    @Transactional(readOnly = true)
    public FontDownloadDto downloadFont(Long fontId) {
        Long userId = SecurityUtils.getCurrentUserId();

        // 폰트 찾기
        FontCreation font = fontCreationRepository.findById(fontId)
                .orElseThrow(() -> new CustomException(FONT_NOT_FOUND));

        // 사용자 권한 확인
        if (!font.getUser().getId().equals(userId)) {
            throw new CustomException(UNAUTHORIZED_ACCESS);
        }

        // S3에서 폰트 파일 가져오기
        byte[] fontData = storageService.downloadFile(font.getS3FontKey());

        return FontDownloadDto.builder()
                .fontName(font.getFontName())
                .fontData(fontData)
                .contentType("font/ttf") // TTF 파일 가정, 실제 폰트 형식에 맞게 조정 필요
                .build();
    }

    @Transactional(readOnly = true)
    public List<FontDownloadDto> downloadFonts(List<Long> fontIds) {
        Long userId = SecurityUtils.getCurrentUserId();

        // 사용자가 소유한 폰트만 필터링하여 가져오기
        List<FontCreation> fonts = fontCreationRepository.findAllById(fontIds)
                .stream()
                .filter(font -> font.getUser().getId().equals(userId))
                .collect(Collectors.toList());

        if (fonts.isEmpty()) {
            throw new CustomException(FONT_NOT_FOUND);
        }

        return fonts.stream()
                .map(font -> {
                    byte[] fontData = storageService.downloadFile(font.getS3FontKey());
                    return FontDownloadDto.builder()
                            .fontName(font.getFontName())
                            .fontData(fontData)
                            .contentType("font/ttf") // TTF 파일 가정
                            .build();
                })
                .collect(Collectors.toList());
    }
}

