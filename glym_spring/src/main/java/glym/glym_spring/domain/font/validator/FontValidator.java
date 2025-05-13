package glym.glym_spring.domain.font.validator;

import glym.glym_spring.domain.font.repository.FontCreationRepository;
import glym.glym_spring.domain.user.domain.User;
import glym.glym_spring.global.exception.domain.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static glym.glym_spring.global.exception.errorcode.ErrorCode.FONT_CREATION_LIMIT_EXCEEDED;
import static glym.glym_spring.global.exception.errorcode.ErrorCode.FONT_NAME_ALREADY_EXISTS;

@Component
@RequiredArgsConstructor
public class FontValidator {

    private final FontCreationRepository fontCreationRepository;

    @Value("${font.limit.per-user:5}")
    private int maxFontsPerUser;

    /**
     * 사용자의 폰트 생성 제한을 검증합니다.
     *
     * @param user 사용자 객체
     * @throws CustomException 폰트 생성 제한을 초과한 경우
     */
    public void validateFontCreationLimit(User user) {
        if (!user.canCreateFont(maxFontsPerUser)) {
            throw new CustomException(FONT_CREATION_LIMIT_EXCEEDED);
        }
    }

    /**
     * 폰트 이름 중복을 검증합니다.
     *
     * @param userId 사용자 ID
     * @param fontName 폰트 이름
     * @throws CustomException 이미 같은 이름의 폰트가 존재하는 경우
     */
    public void validateFontNameDuplicate(Long userId, String fontName) {
        if (fontCreationRepository.existsByUserIdAndFontName(userId, fontName)) {
            throw new CustomException(FONT_NAME_ALREADY_EXISTS);
        }
    }
}