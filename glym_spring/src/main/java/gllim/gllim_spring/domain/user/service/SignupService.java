package gllim.gllim_spring.domain.user.service;

import gllim.gllim_spring.domain.user.dto.SignupRequestDto;
import gllim.gllim_spring.domain.user.domain.User;
import gllim.gllim_spring.global.exception.CustomException;
import gllim.gllim_spring.global.exception.ErrorCode;
import gllim.gllim_spring.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignupService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signUp(SignupRequestDto signupRequestDto) throws CustomException {

        if (userRepository.findByEmail(signupRequestDto.getEmail()).isPresent()) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User newUser = User.builder()
                .username(signupRequestDto.getUsername())
                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                .email(signupRequestDto.getEmail())
                .phone(signupRequestDto.getPhone())
                .build();

        userRepository.save(newUser);
    }

}
