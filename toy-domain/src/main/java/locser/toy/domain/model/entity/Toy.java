package locser.toy.domain.model.entity;

import jakarta.persistence.*;
import locser.toy.domain.model.enums.ToyConditionStatus;
import locser.toy.domain.model.enums.ToyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity đại diện cho Đồ chơi trong hệ thống trao đổi đồ chơi.
 * Lưu trữ thông tin về đồ chơi, tình trạng, danh mục và các thông tin liên quan đến việc trao đổi.
 */
@Entity
@Table(name = "toys")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Toy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "toy_id")
    private Long id;

    @Column(name = "user_id", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long userId = 0L;

    @Column(name = "campaign_id", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long campaignId = 0L;

    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT ''")
    private String name = "";

    @Column(name = "description", nullable = false, columnDefinition = "TEXT DEFAULT ''")
    private String description = "";

    @Column(name = "category", nullable = false, columnDefinition = "VARCHAR(100) DEFAULT ''")
    private String category = "";

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "condition", nullable = false, columnDefinition = "INT DEFAULT 0")
    private ToyConditionStatus condition = ToyConditionStatus.NEW;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false, columnDefinition = "INT DEFAULT 0")
    private ToyStatus status = ToyStatus.AVAILABLE;

    @Column(name = "desired_exchange_items", nullable = false, columnDefinition = "TEXT DEFAULT ''")
    private String desiredExchangeItems = "";

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
