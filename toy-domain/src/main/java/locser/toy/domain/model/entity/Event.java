package locser.toy.domain.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event extends DateAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description", nullable = false, columnDefinition = "TEXT")
  private String description = "";

  @Column(name = "start_date", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
  private LocalDateTime startDate;

  @Column(name = "end_date", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
  private LocalDateTime endDate;

  @Column(name = "theme", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT ''")
  private String theme;

  @Column(name = "rules", nullable = false, columnDefinition = "JSON")
  private String rules;

  @Column(name = "status", nullable = false, columnDefinition = "INT DEFAULT 0")
  private Integer status;

  @Column(name = "type", nullable = false, columnDefinition = "INT DEFAULT 1")
  private Integer type = 1; // Default to EXCHANGE

  // @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME
  // DEFAULT CURRENT_TIMESTAMP")
  // @Cre
  // private LocalDateTime createdAt;

  // @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME
  // DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
  // private LocalDateTime updatedAt;

  @Version
  private Long version = 0L;
}
