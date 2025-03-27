package gllim.gllim_spring.domain.font.domain;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "font_id", nullable = false)
    private Font font;

    @Column(name = "s3_image_key", nullable = false)
    private String s3ImageKey;

    @Column(name = "character_code", nullable = false)
    private String characterCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
