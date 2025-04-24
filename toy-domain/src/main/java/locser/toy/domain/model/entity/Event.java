package locser.toy.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import locser.toy.domain.model.enums.EventStatus;
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
public class Event {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description", nullable = false, columnDefinition = "INT DEFAULT ''")
  private String description;

  @Column(name = "start_date", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
  private LocalDateTime startDate;

  @Column(name = "end_date", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
  private LocalDateTime endDate;

  @Column(name = "theme", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT ''")
  private String theme;

  @Column(name = "rules", nullable = false, columnDefinition = "JSON")
  private String rules;

  @Enumerated(EnumType.ORDINAL) // Store enum as number (ordinal)
  @Column(name = "status", nullable = false, columnDefinition = "INT DEFAULT 0")
  private EventStatus status = EventStatus.UPCOMING;


  @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
  private LocalDateTime updatedAt;

  @Version
  private Long version = 0L;
}

// //CREATE TABLE events (
// campaign_id BIGINT AUTO_INCREMENT PRIMARY KEY,
// name VARCHAR(255) NOT NULL,
// description TEXT NULL,
// start_date DATETIME NOT NULL,
// end_date DATETIME NOT NULL,
// theme VARCHAR(100) NULL,
// rules JSON NULL, -- Lưu trữ các quy tắc phức tạp (số lượng đồ chơi tối đa,
// loại, điều kiện...)
// status ENUM('UPCOMING', 'ACTIVE', 'PAST', 'DELETED') NOT NULL DEFAULT
// 'UPCOMING',
// created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
// updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
// );

// ALTER TABLE events ADD INDEX idx_events_status (status);
// ALTER TABLE events ADD INDEX idx_events_dates (start_date, end_date);