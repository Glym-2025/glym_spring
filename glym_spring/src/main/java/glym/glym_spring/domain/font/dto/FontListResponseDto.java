package glym.glym_spring.domain.font.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FontListResponseDto {
    private Long id;
    private String fontName;
    private LocalDateTime createdAt;
    private String fontUrl;
}