package glym.glym_spring.domain.font.utils;

import glym.glym_spring.global.exception.errorcode.ErrorCode;
import glym.glym_spring.global.exception.domain.ImageValidationException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class ImageConverter {

    /**
     * JPG/JPEG 이미지를 PNG로 변환하거나, PNG 이미지는 그대로 반환합니다.
     *
     * @param file 입력 이미지 (JPG/JPEG 또는 PNG, MultipartFile)
     * @return PNG 이미지의 바이트 배열
     * @throws Exception 파일 형식이 잘못되었거나 변환 중 오류 발생 시
     */
    public static byte[] convertToPng(MultipartFile file) throws IOException, ImageValidationException {
        String originalFilename = file.getOriginalFilename();
        boolean isPng = originalFilename != null &&
                originalFilename.toLowerCase().endsWith(".png");

        if (isPng) {
            // PNG 파일이면 원본 반환
            return file.getBytes();
        }

        // JPG/JPEG 파일이면 PNG로 변환
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        if (bufferedImage == null) {
            throw new ImageValidationException(ErrorCode.INVALID_IMAGE);
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "png", outputStream);
            return outputStream.toByteArray();
        }
    }
}