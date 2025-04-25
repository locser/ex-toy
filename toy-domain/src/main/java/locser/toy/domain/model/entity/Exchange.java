package locser.toy.domain.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import locser.toy.domain.model.enums.ExchangeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing an Exchange in the toy exchange system.
 */
@Entity
@Table(name = "exchanges")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Exchange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "campaign_id", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long campaignId = 0L;

    @Column(name = "requester_id", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long requesterId = 0L;

    @Column(name = "requester_toy_id", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long requesterToyId = 0L;

    @Column(name = "owner_id", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long ownerId = 0L;

    @Column(name = "owner_toy_id", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long ownerToyId = 0L;

    @Column(name = "status", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer status = ExchangeStatus.REQUESTED.getValue();

    @Column(name = "request_message", nullable = false, columnDefinition = "TEXT")
    private String requestMessage = "";

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
