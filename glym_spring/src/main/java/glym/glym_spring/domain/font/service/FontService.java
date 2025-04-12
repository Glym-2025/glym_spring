package glym.glym_spring.domain.font.service;

import glym.glym_spring.domain.aiserverclient.domain.AIServerClient;
import glym.glym_spring.domain.font.domain.FontCreation;
import glym.glym_spring.domain.font.repository.FontCreationRepository;
import glym.glym_spring.domain.font.validator.HandWritingImageValidator;
import glym.glym_spring.domain.s3stroage.service.S3StorageService;
import glym.glym_spring.global.exception.domain.ImageValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
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

    public String createFont(MultipartFile handWritingImage, String fontName) throws ImageValidationException {


        Long userId=1L;
        // 2. 현재 사용자 ID 가져오기 (Spring Security 사용)
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//        Long userId = userDetails.getUserId();

        String uuid = UUID.randomUUID().toString();

        FontCreation fontCreation = processingImage(handWritingImage, fontName, userId,uuid);

        aiServerClient.sendToAIServer(
                fontCreation.getS3ImageKey(),
                fontName,
                userId,
                fontCreation.getId());

        // 작업요청 을 생성 유저 아이디, s3key (이미지경로), 작업아이디, 스테이터스, 생성시간
        return uuid;
    }




    public FontCreation processingImage (MultipartFile handWritingImage, String fontName, Long userId, String uuid) throws ImageValidationException {
        //handWritingImageValidator.validate(handWritingImage);

        String s3Key = s3StorageService.storeImage(handWritingImage, uuid, fontName,userId);

        FontCreation image = FontCreation.builder()
                //.userId(userId)
                .fontName(fontName)
                .s3ImageKey(s3Key)
                .createdAt(LocalDateTime.now())
                .status(PROCESSING)
                .createdAt(LocalDateTime.now())
                .build();

        return fontCreationRepository.save(image);
    }


}
