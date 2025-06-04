package locser.toy.domain.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a Toy Participation in giveaway campaigns.
 */
@Entity
@Table(name = "toy_participations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ToyParticipation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "toy_id", nullable = false)
    private Long toyId;

    @Column(name = "campaign_id", nullable = false)
    private Long campaignId;

    @Column(name = "participation_date", nullable = false)
    private LocalDateTime participationDate;

    @Column(name = "status", nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer status = 1; // Default to CLAIMED

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
