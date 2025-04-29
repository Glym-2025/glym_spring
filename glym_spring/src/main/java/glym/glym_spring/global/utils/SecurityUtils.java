package glym.glym_spring.global.utils;


import glym.glym_spring.auth.dto.CustomUserDetails;
import glym.glym_spring.global.exception.domain.CustomException;
import glym.glym_spring.global.exception.errorcode.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
        // 인스턴스화 방지
    }

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.USER_NOT_AUTHENTICATED);
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) {
            throw new CustomException(ErrorCode.USER_INVALID_PRINCIPAL);
        }

        return ((CustomUserDetails) principal).getId();
    }

    // 사용자 정보 반환
    public static CustomUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.USER_NOT_AUTHENTICATED);
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) {
            throw new CustomException(ErrorCode.USER_INVALID_PRINCIPAL);
        }

        return (CustomUserDetails) principal;
    }
}