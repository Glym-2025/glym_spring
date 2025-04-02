package glym.glym_spring.domain.user.controller;

import glym.glym_spring.domain.user.dto.SignupRequestDto;
import glym.glym_spring.domain.user.dto.SignupResponseDto;
import glym.glym_spring.domain.user.service.SignupService;
import glym.glym_spring.global.docs.SignupControllerDocs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SignupController implements SignupControllerDocs {

    private final SignupService signupService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signupProcess(@RequestBody SignupRequestDto requestDto) {

        signupService.signUp(requestDto);
        log.info("User: {}", requestDto.getUsername());

        SignupResponseDto response = SignupResponseDto.builder()
                .message("User Signup successful")
                .username(requestDto.getUsername()).build();

        return ResponseEntity.ok(response);
    }
}
