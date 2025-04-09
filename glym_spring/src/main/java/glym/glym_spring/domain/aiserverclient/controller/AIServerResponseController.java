package glym.glym_spring.domain.aiserverclient.controller;

import glym.glym_spring.domain.font.domain.FontProcessingJob;
import glym.glym_spring.domain.font.repository.FontProcessingJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api")
public class AIServerResponseController {

    private final FontProcessingJobRepository fontProcessingJobRepository;

    @PostMapping("/callback")
    public ResponseEntity<Void> handleCallback(@RequestBody Map<String, Object> response) {
        Long jobId = Long.valueOf(response.get("job_id").toString());
        String status = response.get("status").toString();
        String result = response.get("result").toString();

        FontProcessingJob job = fontProcessingJobRepository.findById(jobId).orElse(null);
        if (job != null) {
            job.setStatusFromString(status);
            job.setFontS3Path(result);
            fontProcessingJobRepository.save(job);
            log.info("Callback processed for jobId: {}", jobId);
        }
        return ResponseEntity.ok().build();
    }

}
