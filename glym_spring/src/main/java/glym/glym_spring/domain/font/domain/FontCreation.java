package glym.glym_spring.domain.font.domain;

import glym.glym_spring.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "font_creations")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FontCreation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "font_creation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

//    @Column(name = "user_id", nullable = false)
//    private Long userId;

    @Column(name = "font_name", nullable = false)
    private String fontName;

    @Column(name = "description")
    private String description;

    @Column(name = "s3_font_key")
    private String s3FontKey;

    @Column(name = "s3_image_key", nullable = false)
    private String s3ImageKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private JobStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

//    @Column(name = "font_type")
//    private String fontType = "CUSTOM"; // CUSTOM, PREMIUM, FREE

    @Column(name = "download_count")
    private Integer downloadCount = 0;

    @Column(name = "is_public")
    private Boolean isPublic = false;


    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "font_tags",
            joinColumns = @JoinColumn(name = "font_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 편의 메서드
    public void addTag(Tag tag) {
        this.tags.add(tag);
        tag.getFontCreations().add(this);
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getFontCreations().remove(this);
    }
}