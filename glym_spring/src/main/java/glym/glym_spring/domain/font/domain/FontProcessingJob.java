package glym.glym_spring.domain.font.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "font_processing_jobs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FontProcessingJob {

    @Id
    @Column(name = "job_id", nullable = false, unique = true)
    private String jobId; // UUID

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "s3_image_key", nullable = false)
    private String s3ImageKey;

    @Column(name = "s3_font_key")
    private String s3FontKey;

    @Column(name = "font_name", nullable = false)
    private String fontName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private JobStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "error_message")
    private String errorMessage;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // 상태 업데이트 메서드
    public void updateStatus(JobStatus newStatus, String errorMessage) {
        validateStatusTransition(newStatus);
        this.status = newStatus;
        this.errorMessage = errorMessage; // null 가능
    }

    // 상태 업데이트 메서드 (에러 메시지 없이)
    public void updateStatus(JobStatus newStatus) {
        updateStatus(newStatus, null);
    }

    // 상태 전환 규칙 검증
    private void validateStatusTransition(JobStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("New status cannot be null");
        }

        switch (this.status) {
            case PENDING:
                if (newStatus != JobStatus.PROCESSING && newStatus != JobStatus.COMPLETED && newStatus != JobStatus.FAILED) {
                    throw new IllegalStateException("Invalid transition from PENDING to " + newStatus);
                }
                break;
            case PROCESSING:
                if (newStatus != JobStatus.COMPLETED && newStatus != JobStatus.FAILED) {
                    throw new IllegalStateException("Invalid transition from PROCESSING to " + newStatus);
                }
                break;
            case COMPLETED:
            case FAILED:
                throw new IllegalStateException("Cannot change status from final state: " + this.status);
            default:
                throw new IllegalStateException("Unknown current status: " + this.status);
        }
    }
}