package glym.glym_spring.domain.user.service;

import glym.glym_spring.domain.user.domain.User;
import glym.glym_spring.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public void updateUserInfo(User user, String username, String phone, String profileImageURL) {
        log.info("Updating user info for user: {}", user.getId());
        user.updateProfile(username, phone, profileImageURL);
        userRepository.save(user);
    }

}
