package glym.glym_spring.domain.font.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "폰트 생성 요청")
@Setter
@Getter
public class FontCreateRequest {
    @Schema(type = "string", format = "binary", description = "손글씨 이미지 파일 (PNG, JPG)")
    private MultipartFile handWritingImage;

    @Schema(description = "생성할 폰트 이름", example = "MyHandFont")
    private String fontName;

    @Schema(description = "폰트 설명", example = "이 폰트는 나의 손글씨를 기반으로 생성되었습니다.")
    private String fontDescription;
}