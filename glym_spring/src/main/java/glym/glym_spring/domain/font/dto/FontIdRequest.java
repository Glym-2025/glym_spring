package glym.glym_spring.domain.font.dto;

// src/main/java/glym/glym_spring/domain/font/dto/FontIdsRequest.java

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "폰트 ID 목록 요청")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FontIdRequest {
    @Schema(description = "선택된 폰트 ID 목록")
    private List<Long> fontIds;
}