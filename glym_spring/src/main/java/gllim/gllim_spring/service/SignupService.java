package gllim.gllim_spring.service;

import gllim.gllim_spring.dto.SignupRequestDto;
import gllim.gllim_spring.entity.UserEntity;
import gllim.gllim_spring.exception.CustomException;
import gllim.gllim_spring.exception.ErrorCode;
import gllim.gllim_spring.repository.UserRepository;
import jakarta.validation.constraints.Email;
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

        UserEntity newUser = UserEntity.builder()
                .username(signupRequestDto.getUsername())
                .password(signupRequestDto.getPassword())
                .email(signupRequestDto.getEmail())
                .build();

        userRepository.save(newUser);
    }

}
