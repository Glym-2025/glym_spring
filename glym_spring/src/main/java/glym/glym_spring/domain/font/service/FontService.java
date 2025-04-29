package glym.glym_spring.domain.font.service;

import glym.glym_spring.domain.aiserverclient.domain.AIServerClient;
import glym.glym_spring.domain.font.domain.FontProcessingJob;
import glym.glym_spring.domain.font.dto.FontCreateRequest;
import glym.glym_spring.domain.font.repository.FontCreationRepository;
import glym.glym_spring.domain.font.repository.FontProcessingJobRepository;
import glym.glym_spring.domain.font.utils.ImageConverter;
import glym.glym_spring.domain.font.validator.HandWritingImageValidator;
import glym.glym_spring.domain.s3stroage.service.S3StorageService;
import glym.glym_spring.domain.user.domain.User;
import glym.glym_spring.domain.user.repository.UserRepository;
import glym.glym_spring.global.exception.domain.CustomException;
import glym.glym_spring.global.exception.domain.ImageValidationException;
import glym.glym_spring.global.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

import static glym.glym_spring.domain.font.domain.JobStatus.PROCESSING;
import static glym.glym_spring.global.exception.errorcode.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FontService {
    private final FontCreationRepository fontCreationRepository;
    private final HandWritingImageValidator handWritingImageValidator;
    private final S3StorageService s3StorageService;
    private final AIServerClient aiServerClient;
    private final FontProcessingJobRepository fontProcessingJobRepository;
    private final UserRepository userRepository;

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

        aiServerClient.sendToAIServer(job);

        return uuid;
    }

    public String processingImage (MultipartFile handWritingImage,  Long userId, String uuid) throws IOException, ImageValidationException {
        //handWritingImageValidator.validate(handWritingImage);
        ImageConverter.convertToPng(handWritingImage);

        return s3StorageService.storeImage(handWritingImage, uuid,userId);

    }




}
