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

/**
 * Entity representing an Exchange Message in the toy exchange system.
 */
@Entity
@Table(name = "exchange_messages")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "exchange_id", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long exchangeId = 0L;

    @Column(name = "sender_id", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long senderId = 0L;

    @Column(name = "message_text", nullable = false, columnDefinition = "TEXT")
    private String messageText = "";

    @Column(name = "sent_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime sentAt = LocalDateTime.now();

    @Version
    private Long version = 0L;
}
