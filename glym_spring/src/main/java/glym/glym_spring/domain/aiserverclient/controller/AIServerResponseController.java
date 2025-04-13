package glym.glym_spring.domain.aiserverclient.controller;

import glym.glym_spring.domain.font.domain.FontCreation;
import glym.glym_spring.domain.font.repository.FontCreationRepository;
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

    private final FontCreationRepository FontCreationRepository;

    @PostMapping("/callback")
    public ResponseEntity<Void> handleCallback(@RequestBody Map<String, Object> response) {
        Long jobId = Long.valueOf(response.get("jobId").toString());
        String status = response.get("status").toString();
        String result = response.get("result").toString();

        FontCreation job = FontCreationRepository.findById(jobId).orElse(null);
        if (job != null) {
            //상태 s3경로 로직구현 필요
            FontCreationRepository.save(job);
            log.info("Callback processed for jobId: {}", jobId);
        }
        return ResponseEntity.ok().build();
    }

}
