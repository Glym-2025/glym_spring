package glym.glym_spring.domain.font.controller;

import glym.glym_spring.domain.font.service.FontService;
import glym.glym_spring.global.exception.domain.ImageValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/font")
@RequiredArgsConstructor
public class FontController {

    private final FontService fontService;

    @GetMapping
    public String fontP() {
        return "font";
    }

    @PostMapping("/upload")
    public ResponseEntity<?> createFont(@RequestParam("handWritingImage") MultipartFile handWritingImage,
                                     @RequestParam("fontName") String fontName) throws ImageValidationException {
        fontService.createFont(handWritingImage, fontName);
        return ResponseEntity.ok().build();
    }


}
