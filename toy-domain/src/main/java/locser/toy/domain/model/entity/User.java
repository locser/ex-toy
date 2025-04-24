package locser.toy.domain.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho Người dùng trong hệ thống trao đổi đồ chơi.
 * Lưu trữ thông tin cá nhân, thông tin đăng nhập và đánh giá của người dùng.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email", nullable = false, unique = true, columnDefinition = "VARCHAR(255) DEFAULT ''")
    private String email = "";

    @Column(name = "password_hash", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT ''")
    private String passwordHash = "";

    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(100) DEFAULT ''")
    private String name = "";

    @Column(name = "location", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT ''")
    private String location = "";

    @Column(name = "is_admin", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isAdmin = false;

    @Column(name = "average_rating", nullable = false, columnDefinition = "DECIMAL(3,2) DEFAULT 0.00")
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Version
    private Long version = 0L;

    /**
     * Updates the updatedAt timestamp before updating the entity.
     */
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
