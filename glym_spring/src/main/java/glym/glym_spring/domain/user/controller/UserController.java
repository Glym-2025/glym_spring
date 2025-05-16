package glym.glym_spring.domain.user.controller;

import glym.glym_spring.domain.auth.dto.CustomUserDetails;
import glym.glym_spring.domain.user.dto.UserInfoResponse;
import glym.glym_spring.domain.user.service.UserService;
import glym.glym_spring.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Object>> getUserInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails, @CookieValue("refreshToken") String refreshToken) {
        log.info("User: {}", customUserDetails.getUser());
        log.info("Refresh Token: {}", refreshToken);
        UserInfoResponse userInfoResponse = UserInfoResponse.builder()
                .email(customUserDetails.getUsername())
                .username(customUserDetails.getUser().getUsername())
                .phone(customUserDetails.getUser().getPhone())
                .profileImageURL(customUserDetails.getUser().getProfileImageURL())
                .build();

        return ResponseEntity.ok(ApiResponse.success(userInfoResponse, "User Info Retrieved"));
    }

    @PatchMapping("/update/profile")
    public ResponseEntity<ApiResponse<Object>> updateUserInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                              @CookieValue("refreshToken") String refreshToken,
                                                              @RequestBody UserInfoResponse userInfoResponse) {
        log.info("User: {}", customUserDetails.getUser());
        log.info("Update Info: {}", userInfoResponse);

        // Update logic here (e.g., update user info in the database)
        userService.updateUserInfo(
                customUserDetails.getUser(),
                userInfoResponse.getUsername(),
                userInfoResponse.getPhone(),
                userInfoResponse.getProfileImageURL()
        );

        return ResponseEntity.ok(ApiResponse.success(userInfoResponse, "User Info Updated"));
    }

}
