package locser.toy.domain.model.entity;

import jakarta.persistence.*;
import locser.toy.domain.model.enums.EntityType;
import locser.toy.domain.model.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity representing a Notification in the toy exchange system.
 */
@Entity
@Table(name = "notifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column(name = "user_id", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long userId = 0L;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type", nullable = false, columnDefinition = "INT DEFAULT 0")
    private NotificationType type = NotificationType.NEW_EXCHANGE_REQUEST;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "related_entity_type", nullable = false, columnDefinition = "INT DEFAULT 0")
    private EntityType relatedEntityType = EntityType.EXCHANGE;

    @Column(name = "related_entity_id", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long relatedEntityId = 0L;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT DEFAULT ''")
    private String message = "";

    @Column(name = "is_read", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isRead = false;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Version
    private Long version = 0L;
}
