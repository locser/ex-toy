package locser.toy.domain.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import locser.toy.domain.model.enums.ToyConditionStatus;
import locser.toy.domain.model.enums.ToyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a Toy in the toy exchange system.
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
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long userId = 0L;

    @Column(name = "campaign_id", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long campaignId = 0L;

    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT ''")
    private String name = "";

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description = "";

    @Column(name = "category", nullable = false, columnDefinition = "VARCHAR(100) DEFAULT ''")
    private String category = "";

    @Column(name = "toy_condition", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer condition = ToyConditionStatus.NEW.getValue();

    @Column(name = "status", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer status = ToyStatus.AVAILABLE.getValue();

    @Column(name = "desired_exchange_items", nullable = false, columnDefinition = "TEXT")
    private String desiredExchangeItems = "";

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();

    /**
     * Updates the updatedAt timestamp before updating the entity.
     */
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
