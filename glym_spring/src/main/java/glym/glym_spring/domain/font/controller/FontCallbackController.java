package glym.glym_spring.domain.font.controller;

import glym.glym_spring.domain.font.dto.AIResultDto;
import glym.glym_spring.domain.font.service.FontCallbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/font")
public class FontCallbackController {

    private final FontCallbackService fontCallbackService;

    @PostMapping("/callback")
    public ResponseEntity<Void> handleCallback(@RequestBody AIResultDto result) {
        log.info("Received callback for jobId: {}", result.getJobId());
        fontCallbackService.processCallback(result);
        return ResponseEntity.ok().build();
    }
}