package glym.glym_spring.domain.font.controller;

import glym.glym_spring.domain.font.docs.FontControllerDocs;
import glym.glym_spring.domain.font.dto.FontCreateRequest;
import glym.glym_spring.domain.font.dto.JobStatusResponseDto;
import glym.glym_spring.domain.font.dto.SuccessResponse;
import glym.glym_spring.domain.font.service.FontService;
import glym.glym_spring.global.exception.domain.ImageValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.Locale;

@Controller
@RequestMapping("/font")
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

    @GetMapping(path = "/jobs/{jobId}/status", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamJobStatus(@PathVariable String jobId) {
        return fontService.getJobStatusStream(jobId)
                .map(this::formatSseEvent);
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
}