package glym.glym_spring.domain.font.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static glym.glym_spring.domain.font.domain.JobStatus.*;

@Entity
@Table(name = "font_processing_job")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FontProcessingJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String imageS3Path;

    @Column(nullable = false)
    private String fontName;

    @Column
    private String fontS3Path;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime completedAt;

public void setFontS3Path(String fontS3Path) {
    this.fontS3Path = fontS3Path;
}

// String을 Enum으로 변환하여 설정하는 메서드
    public void setStatusFromString(String statusString) {
        if (statusString != null && !statusString.trim().isEmpty()) {
            try {
                // String을 대문자로 변환 후 Enum.valueOf로 매핑
                this.status = valueOf(statusString.toUpperCase());
            } catch (IllegalArgumentException e) {
                // 유효하지 않은 값일 경우 기본값 설정
                this.status = PENDING;
            }
        } else {
            // null이거나 빈 문자열일 경우 기본값 설정
            this.status = PENDING;
        }
    }

}