// src/main/java/glym/glym_spring/domain/font/dto/FontDownloadResponseDto.java
package glym.glym_spring.domain.font.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "폰트 다운로드 응답")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FontDownloadResponseDto {
    @Schema(description = "폰트 ID")
    private Long id;

    @Schema(description = "폰트 이름")
    private String fontName;

    @Schema(description = "폰트 다운로드 URL")
    private String downloadUrl;
}