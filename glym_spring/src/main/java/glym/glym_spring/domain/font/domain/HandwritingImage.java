package glym.glym_spring.domain.font.domain;

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
    private FontCreation fontCreation;

    @Column(name = "s3_image_key", nullable = false)
    private String s3ImageKey;


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
