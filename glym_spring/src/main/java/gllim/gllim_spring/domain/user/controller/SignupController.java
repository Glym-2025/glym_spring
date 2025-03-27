package gllim.gllim_spring.domain.user.controller;

import gllim.gllim_spring.domain.user.dto.SignupRequestDto;
import gllim.gllim_spring.domain.user.service.SignupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SignupController {

    private final SignupService signupService;

    @PostMapping("/signup")
    public ResponseEntity<String> signupProcess(@RequestBody SignupRequestDto requestDto) {
        log.info("User: {}", requestDto.getUsername());

        return ResponseEntity.ok("User signup successful");
    }
}
