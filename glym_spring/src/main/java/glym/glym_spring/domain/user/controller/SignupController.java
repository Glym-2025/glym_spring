package glym.glym_spring.domain.user.controller;

import glym.glym_spring.global.dto.ApiResponse;
import glym.glym_spring.domain.user.dto.SignupRequestDto;
import glym.glym_spring.domain.user.dto.SignupResponseDto;
import glym.glym_spring.domain.user.service.SignupService;
import glym.glym_spring.global.docs.SignupControllerDocs;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SignupController implements SignupControllerDocs {

    private final SignupService signupService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Object>> signupProcess(@RequestBody @Valid SignupRequestDto requestDto) {

        signupService.signUp(requestDto);
        log.info("User: {}", requestDto.getUsername());

        SignupResponseDto signupResponseDto = SignupResponseDto.builder()
                .username(requestDto.getUsername())
                .email(requestDto.getEmail())
                .build();

        return ResponseEntity.ok(ApiResponse.success(signupResponseDto, "Signup Success"));
    }

    @GetMapping("/signup/check-email")
    public ResponseEntity<ApiResponse<Object>> checkEmailDuplicate(@RequestParam String email) {
        signupService.checkEmail(email);
        log.info("User: {}", email);

        return ResponseEntity.ok(ApiResponse.success(email, "Email Available"));
    }
}
