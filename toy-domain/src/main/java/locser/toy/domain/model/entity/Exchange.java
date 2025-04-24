package locser.toy.domain.model.entity;

import jakarta.persistence.*;
import locser.toy.domain.model.enums.ExchangeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
    @Column(name = "exchange_id")
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

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false, columnDefinition = "INT DEFAULT 0")
    private ExchangeStatus status = ExchangeStatus.REQUESTED;

    @Column(name = "request_message", nullable = false, columnDefinition = "TEXT DEFAULT ''")
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
