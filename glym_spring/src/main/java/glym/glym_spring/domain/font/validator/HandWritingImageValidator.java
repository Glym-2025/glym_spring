package glym.glym_spring.domain.font.validator;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import glym.glym_spring.global.exception.errorcode.ErrorCode;
import glym.glym_spring.global.exception.domain.ImageValidationException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class HandWritingImageValidator {

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList("image/png", "image/jpeg");
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("png", "jpeg", "jpg");
    private static final int MIN_TEXT_SIZE = 128; // 최소 글자 크기 (픽셀)
    private static final int MIN_SENTENCE_SIZE = 512; // 최소 문장 크기 (픽셀)
    private static final int MAX_SENTENCE_SIZE = 1024; // 최대 문장 크기 (픽셀)
    private static final int MIN_RESOLUTION_DPI = 300; // 최소 해상도 (DPI)

    /**
     * 이미지 파일의 전체 검증을 수행하는 메인 메소드
     * @param file 검증할 MultipartFile
     * @throws ImageValidationException 검증 실패 시 발생
     */
    public void validate(MultipartFile file) throws ImageValidationException {
        if (file == null || file.isEmpty()) {
            throw new ImageValidationException(ErrorCode.FILE_NOT_FOUND);
        }

        checkFileExtension(file);
        BufferedImage image = toBufferedImage(file);
        checkImageSize(image);
        checkResolution(file);
    }

    /**
     * 파일 확장자 및 Content-Type 검증
     * @param file 검증할 MultipartFile
     * @throws ImageValidationException 검증 실패 시 발생
     */
    private void checkFileExtension(MultipartFile file) throws ImageValidationException {
        // Content-Type 검증
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new ImageValidationException(ErrorCode.INVALID_FILE_FORMAT);
        }

        // 파일 확장자 검증
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new ImageValidationException(ErrorCode.INVALID_FILE_NAME);
        }

        String extension = getExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new ImageValidationException(ErrorCode.INVALID_FILE_FORMAT);
        }
    }

    /**
     * 파일을 BufferedImage로 변환
     * @param file 변환할 MultipartFile
     * @return 변환된 BufferedImage
     * @throws ImageValidationException 변환 실패 시 발생
     */
    private BufferedImage toBufferedImage(MultipartFile file) throws ImageValidationException {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new ImageValidationException(ErrorCode.INVALID_IMAGE);
            }
            return image;
        } catch (IOException e) {
            throw new ImageValidationException(ErrorCode.IO_ERROR);
        }
    }

    /**
     * 이미지 크기 검증
     * @param image 검증할 BufferedImage
     * @throws ImageValidationException 검증 실패 시 발생
     */
    private void checkImageSize(BufferedImage image) throws ImageValidationException {
        // 글자 크기 검증 (최소 128x128)
        if (image.getWidth() < MIN_TEXT_SIZE || image.getHeight() < MIN_TEXT_SIZE) {
            throw new ImageValidationException(ErrorCode.TEXT_SIZE_TOO_SMALL);
        }

        // 문장 크기 검증 (512~1024)
        if (image.getWidth() < MIN_SENTENCE_SIZE || image.getHeight() < MIN_SENTENCE_SIZE) {
            throw new ImageValidationException(ErrorCode.SENTENCE_SIZE_TOO_SMALL);
        }

        if (image.getWidth() > MAX_SENTENCE_SIZE || image.getHeight() > MAX_SENTENCE_SIZE) {
            throw new ImageValidationException(ErrorCode.SENTENCE_SIZE_TOO_LARGE);
        }
    }

    /**
     * 이미지 해상도(DPI) 검증
     * @param file 검증할 MultipartFile
     * @throws ImageValidationException 검증 실패 시 발생
     */
    private void checkResolution(MultipartFile file) throws ImageValidationException {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file.getInputStream());
            ExifIFD0Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

            if (directory != null && directory.containsTag(ExifIFD0Directory.TAG_X_RESOLUTION)) {
                int xDpi = directory.getRational(ExifIFD0Directory.TAG_X_RESOLUTION).intValue();
                int yDpi = directory.getRational(ExifIFD0Directory.TAG_Y_RESOLUTION).intValue();

                if (xDpi < MIN_RESOLUTION_DPI || yDpi < MIN_RESOLUTION_DPI) {
                    throw new ImageValidationException(ErrorCode.RESOLUTION_TOO_LOW);
                }
            } else {
                // 해상도 정보가 없는 경우
                throw new ImageValidationException(ErrorCode.RESOLUTION_UNKNOWN);
            }
        } catch (IOException e) {
            throw new ImageValidationException(ErrorCode.IO_ERROR);
        } catch (ImageValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new ImageValidationException(ErrorCode.UNKNOWN_ERROR);
        }
    }

    /**
     * 파일명에서 확장자 추출
     * @param filename 파일명
     * @return 확장자
     */
    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
    }
}