package glym.glym_spring.domain.handwritingimage.service;
import glym.glym_spring.domain.font.domain.JobStatus;
import glym.glym_spring.domain.font.validator.HandWritingImageValidator;
import glym.glym_spring.domain.handwritingimage.domain.HandwritingImage;
import glym.glym_spring.domain.handwritingimage.repository.HandWritingImageRepository;
import glym.glym_spring.domain.s3stroage.service.S3StorageService;
import glym.glym_spring.global.exception.domain.ImageValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HandWritingImageService {
    private final S3StorageService s3StorageService;
    private final HandWritingImageValidator handWritingImageValidator;
    private final HandWritingImageRepository handWritingImageRepository;

    public String processingImage (MultipartFile handWritingImage, String fontName) throws ImageValidationException {
        //handWritingImageValidator.validate(handWritingImage);

        String uuid = UUID.randomUUID().toString();
        Long userId=1L;
        String s3Key = s3StorageService.storeImage(handWritingImage, uuid, fontName,userId);

        // 2. 현재 사용자 ID 가져오기 (Spring Security 사용)
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//        Long userId = userDetails.getUserId();


        HandwritingImage image = HandwritingImage.builder()
                .id(userId)
                .s3ImageKey(s3Key)
                .createdAt(LocalDateTime.now())
                .status(JobStatus.PROCESSING)
                .build();

        handWritingImageRepository.save(image);

        return s3Key;
    }
}
