package glym.glym_spring.domain.font.controller;

import glym.glym_spring.domain.auth.dto.CustomUserDetails;
import glym.glym_spring.domain.font.docs.FontControllerDocs;
import glym.glym_spring.domain.font.dto.*;
import glym.glym_spring.domain.font.service.FontService;
import glym.glym_spring.global.exception.domain.ImageValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
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
        public ResponseEntity<?> createFont(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                        @ModelAttribute FontCreateRequest request) throws ImageValidationException, IOException {
        long userId = customUserDetails.getId();
        String jobId = fontService.createFont(userId, request);
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
    public ResponseEntity<?> getUserFonts(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(fontService.getUserFonts(customUserDetails.getId()));
    }
    @Override
    @PostMapping("/fonts/download")
    public ResponseEntity<?> downloadFonts(   @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                              @RequestBody FontIdsRequest request) {
        List<FontDownloadResponseDto> downloads = fontService.getDownloadUrlsForFonts(customUserDetails.getId(), request.getFontIds());
        return ResponseEntity.ok(downloads);
    }

    @Override
    @DeleteMapping("/fonts/delete")
    public ResponseEntity<?> deleteFonts(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                         @RequestBody FontIdsRequest request) {
        fontService.deleteFonts(customUserDetails.getId(), request.getFontIds());
        String message = messageSource.getMessage("font.delete.success", null, Locale.getDefault());
        return ResponseEntity.ok(new SuccessResponse(message, null));
    }



}
