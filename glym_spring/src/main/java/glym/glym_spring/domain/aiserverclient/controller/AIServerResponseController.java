package glym.glym_spring.domain.aiserverclient.controller;

import glym.glym_spring.domain.aiserverclient.dto.AIResultDto;
import glym.glym_spring.domain.font.domain.FontCreation;
import glym.glym_spring.domain.font.domain.FontProcessingJob;
import glym.glym_spring.domain.font.domain.JobStatus;
import glym.glym_spring.domain.font.repository.FontCreationRepository;
import glym.glym_spring.domain.font.repository.FontProcessingJobRepository;
import glym.glym_spring.domain.user.domain.User;
import glym.glym_spring.domain.user.repository.UserRepository;
import glym.glym_spring.global.exception.domain.CustomException;
import glym.glym_spring.global.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static glym.glym_spring.global.exception.errorcode.ErrorCode.USER_NOT_FOUND;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api")
public class AIServerResponseController {

    private final FontCreationRepository FontCreationRepository;
    private final FontProcessingJobRepository fontProcessingJobRepository;
    private final UserRepository userRepository;

    @PostMapping("/callback")
    public ResponseEntity<Void> handleCallback(@RequestBody AIResultDto result) {
        String jobId = result.getJobId();
        JobStatus jobStatus = JobStatus.fromString(result.getStatus());
        String s3FontPath = result.getS3FontPath();

        FontProcessingJob job = fontProcessingJobRepository.findById(jobId).orElse(null);
        job.updateStatus(jobStatus);
        System.out.println("result = " + result);
        if (job != null) {
            Long userId = SecurityUtils.getCurrentUserId();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

            FontCreation fontCreation = FontCreation.builder()
                    .fontName(job.getFontName())
                    .user(user)
                    .s3FontKey(s3FontPath)
                    .s3ImageKey(job.getS3ImageKey())
                    .build();

            log.info("Callback processed for jobId: {}", jobId);
        }
        return ResponseEntity.ok().build();
    }

}
