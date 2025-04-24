package locser.toy.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import locser.toy.domain.model.enums.DisputeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a Dispute in the toy exchange system.
 */
@Entity
@Table(name = "disputes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Dispute {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "exchange_id", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
  private Long exchangeId = 0L;

  @Column(name = "reporter_id", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
  private Long reporterId = 0L;

  @Column(name = "reason", nullable = false, columnDefinition = "TEXT DEFAULT ''")
  private String reason = "";

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "status", nullable = false, columnDefinition = "INT DEFAULT 0")
  private DisputeStatus status = DisputeStatus.OPEN;

  @Column(name = "resolution_details", nullable = false, columnDefinition = "TEXT DEFAULT ''")
  private String resolutionDetails = "";

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