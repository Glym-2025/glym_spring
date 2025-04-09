package glym.glym_spring.domain.handwritingimage.domain;

import glym.glym_spring.domain.font.domain.Font;
import glym.glym_spring.domain.font.domain.JobStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static glym.glym_spring.domain.font.domain.JobStatus.*;

@Entity
@Table(name = "handwriting_images")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HandwritingImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "font_id", nullable = true)
    private Font font;

    @Column(name = "s3_image_key", nullable = false)
    private String s3ImageKey;

    @Version
    @Column(name = "version", nullable = false)
    private Long version=0L;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private JobStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.status == null) {
            this.status = PROCESSING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
