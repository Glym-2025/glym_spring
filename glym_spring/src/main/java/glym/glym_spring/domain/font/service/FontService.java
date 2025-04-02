package glym.glym_spring.domain.font.service;

import glym.glym_spring.domain.font.repository.FontRepository;
import glym.glym_spring.domain.font.repository.HandWritingImageRepository;
import glym.glym_spring.domain.font.validator.HandWritingImageValidator;
import glym.glym_spring.global.exception.domain.ImageValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FontService {
    private final FontRepository fontRepository;
    private final HandWritingImageRepository handWritingImageRepository;
    private final HandWritingImageValidator handWritingImageValidator;
    private final S3StorageService s3StorageService;

    public void createFont(MultipartFile handWritingImage, String fontName) throws ImageValidationException {
        //handWritingImageValidator.validate(handWritingImage);
        String uuid = UUID.randomUUID().toString();
        s3StorageService.storeImage(handWritingImage, uuid, fontName);

    }



}
