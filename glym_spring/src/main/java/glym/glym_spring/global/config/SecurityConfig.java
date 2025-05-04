package glym.glym_spring.global.config;

import glym.glym_spring.auth.service.RefreshTokenService;
import glym.glym_spring.global.filter.JWTFilter;
import glym.glym_spring.global.filter.LoginFilter;
import glym.glym_spring.global.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtil jwtUtil;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, RefreshTokenService refreshTokenService) throws Exception {

        LoginFilter loginFilter = new LoginFilter(
                jwtUtil,
                authenticationManager(authenticationConfiguration),
                refreshTokenService);
        loginFilter.setFilterProcessesUrl("/auth/login");

        http
                // cors 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // csrf disable
                .csrf((auth) -> auth.disable())
                //Form 로그인 방식 disable
                .formLogin((auth) -> auth.disable())
                //http basic 인증 방식 disable
                .httpBasic((auth) -> auth.disable())
                //경로별 인가 작업
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login",
                                "/signup",
                                "/signup/**",
                                "/auth/login",
                                "/auth/refresh",
                                "/auth/send-email",
                                "auth/verify-email",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/swagger-ui.html",
                                "/swagger-custom.css",
                                "/static/**",
                               // "/font/**",
                                "/api/callback").permitAll()

                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated())
                //세션 설정
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)

                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * 인증 메니저 설정
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration)
            throws Exception {

        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:5173"
                ,"http://localhost:3000",
                "http://localhost:8080",
                "http://ec2-15-164-102-179.ap-northeast-2.compute.amazonaws.com:8080")); // 허용할 오리진 TODO: CORS 경로 설정 "http://localhost:5173"

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")); // 허용할 HTTP 메서드
        configuration.setAllowCredentials(true); // 인증 정보 포함 여부
        configuration.setAllowedHeaders(Collections.singletonList("*")); // 허용할 헤더
        configuration.setExposedHeaders(Collections.singletonList("*"));
        configuration.setMaxAge(3600L); // Preflight 캐싱 시간

        // 모든 경로에 대해 CORS 설정 적용
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", configuration);
        return urlBasedCorsConfigurationSource;
    }
}


