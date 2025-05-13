package glym.glym_spring.global.infrastructure.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import glym.glym_spring.global.exception.domain.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;

import static glym.glym_spring.global.exception.errorcode.ErrorCode.IMAGE_SAVE_ERROR;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    private final AmazonS3 s3Client;
    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    public String storeImage(MultipartFile file, String uuid, Long userId) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (extension == null || extension.isEmpty()) {
            log.error("Invalid file extension for file: {}", file.getOriginalFilename());
            throw new CustomException(IMAGE_SAVE_ERROR);
        }

        String key = String.format("%s/%s.%s", userId, uuid, extension);
        String timestamp = String.valueOf(System.currentTimeMillis());

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            metadata.addUserMetadata("uploadTime", timestamp);

            s3Client.putObject(new PutObjectRequest(bucketName, key, file.getInputStream(), metadata));
            log.info("Successfully uploaded image to S3. key: s3://handwritingImage/{}/{}", bucketName, key);

            return String.format("s3://handwritingImage/%s/%s", bucketName, key);
        } catch (IOException e) {
            log.error("Failed to upload image to S3. key: s3://{}/{}, error: {}", bucketName, key, e.getMessage());
            throw new CustomException(IMAGE_SAVE_ERROR);
        }
    }

    public String generatePresignedUrl(String objectKey) {
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