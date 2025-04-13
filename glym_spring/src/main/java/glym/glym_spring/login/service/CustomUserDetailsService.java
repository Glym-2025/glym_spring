package glym.glym_spring.login.service;

import glym.glym_spring.domain.user.domain.User;
import glym.glym_spring.domain.user.repository.UserRepository;
import glym.glym_spring.login.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // DB에서 조회
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));

        if (user != null) {

            // UserDetails에 담아서 return 하면 AuthenticationManager가 검증함
            return new CustomUserDetails(user);
        }

        return null;
    }
}
