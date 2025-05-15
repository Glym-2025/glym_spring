package glym.glym_spring.domain.font.controller;

import glym.glym_spring.domain.font.docs.FontControllerDocs;
import glym.glym_spring.domain.font.dto.FontCreateRequest;
import glym.glym_spring.domain.font.dto.JobStatusResponseDto;
import glym.glym_spring.domain.font.dto.SuccessResponse;
import glym.glym_spring.domain.font.service.FontService;
import glym.glym_spring.global.exception.domain.ImageValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Locale;

@Controller
@RequestMapping("/api/font")
@RequiredArgsConstructor
public class FontController implements FontControllerDocs {

    private final FontService fontService;
    private final MessageSource messageSource;

    @GetMapping
    public String fontP() {
        return "font";
    }

    @Override
    @PostMapping("/upload")
    public ResponseEntity<?> createFont(@ModelAttribute FontCreateRequest request) throws ImageValidationException, IOException {
        String jobId = fontService.createFont(request);
        String message = messageSource.getMessage("font.created.success", null, Locale.getDefault());
        return ResponseEntity.ok(new SuccessResponse(message,jobId));
    }

    @Override
    @GetMapping(value = "/{jobId}/status", produces = "text/event-stream")
    public SseEmitter streamJobStatus(@PathVariable String jobId) {
        SseEmitter emitter = new SseEmitter(30_000L); // 30초 타임아웃
        new Thread(() -> {
            try {
                for (JobStatusResponseDto dto : fontService.getJobStatusIterable(jobId)) {
                    emitter.send(formatSseEvent(dto));
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();
        return emitter;
    }

    private String formatSseEvent(JobStatusResponseDto dto) {
        StringBuilder event = new StringBuilder("data: ");
        event.append("{")
                .append("\"status\": \"").append(dto.getStatus()).append("\"");
        if (dto.getFontUrl() != null) {
            event.append(", \"fontUrl\": \"").append(dto.getFontUrl()).append("\"");
        }
        if (dto.getErrorMessage() != null) {
            event.append(", \"errorMessage\": \"").append(dto.getErrorMessage()).append("\"");
        }
        event.append("}\n\n");
        return event.toString();
    }
    @Override
    @GetMapping("/fonts")
    public ResponseEntity<?> getUserFonts() {
        return ResponseEntity.ok(fontService.getUserFonts());
    }



}
