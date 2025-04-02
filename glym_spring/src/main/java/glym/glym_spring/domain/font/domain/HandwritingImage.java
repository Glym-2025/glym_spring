package glym.glym_spring.domain.font.domain;

import glym.glym_spring.domain.font.domain.Font;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "handwriting_images")
@Getter
@Setter
public class HandwritingImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 검증 시점에는 fontId가 없을 수 있으므로 nullable=true로 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "font_id", nullable = true)
    private Font font;

    // 또는 Font 대신 폰트명만 저장하는 방식
    @Column(name = "font_name", nullable = false)
    private String fontName;

    @Column(name = "s3_image_key", nullable = false)
    private String s3ImageKey;

    @Column(name = "character_code", nullable = false)
    private String characterCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ImageStatus status = ImageStatus.VALIDATING; // VALIDATING, PROCESSING, COMPLETED, FAILED

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

