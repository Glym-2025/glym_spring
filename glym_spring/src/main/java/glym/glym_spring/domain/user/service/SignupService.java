package glym.glym_spring.domain.user.service;

import glym.glym_spring.domain.user.dto.SignupRequestDto;
import glym.glym_spring.domain.user.domain.User;
import glym.glym_spring.global.exception.CustomExceptionRefactor;
import glym.glym_spring.domain.user.repository.UserRepository;
import glym.glym_spring.global.exception.errorcode.ErrorCodeRefactor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignupService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signUp(SignupRequestDto signupRequestDto) throws CustomExceptionRefactor {

        if (userRepository.findByEmail(signupRequestDto.getEmail()).isPresent()) {
            throw new CustomExceptionRefactor(ErrorCodeRefactor.EMAIL_ALREADY_EXISTS);
        }
        if(userRepository.findByPhone(signupRequestDto.getPhone()).isPresent()) {
            throw new CustomExceptionRefactor(ErrorCodeRefactor.PHONENUMBER_ALREADY_EXISTS);
        }

        User newUser = User.builder()
                .username(signupRequestDto.getUsername())
                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                .email(signupRequestDto.getEmail())
                .phone(signupRequestDto.getPhone())
                .build();

        userRepository.save(newUser);
    }

    public void checkEmail (String email) throws CustomExceptionRefactor {
        if(userRepository.findByEmail(email).isPresent()) {
            throw new CustomExceptionRefactor(ErrorCodeRefactor.EMAIL_ALREADY_EXISTS, email);
        }
    }

}
