package glym.glym_spring.domain.font.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FontDownloadDto {
    private String fontName;
    private byte[] fontData;
    private String contentType;
}