package locser.toy.domain.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity đại diện cho Tin nhắn trao đổi trong hệ thống trao đổi đồ chơi.
 * Lưu trữ các tin nhắn trao đổi giữa người dùng trong quá trình thực hiện giao dịch trao đổi đồ chơi.
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
    @Column(name = "message_id")
    private Long id;

    @Column(name = "exchange_id", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long exchangeId = 0L;

    @Column(name = "sender_id", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long senderId = 0L;

    @Column(name = "message_text", nullable = false, columnDefinition = "TEXT DEFAULT ''")
    private String messageText = "";

    @Column(name = "sent_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime sentAt = LocalDateTime.now();

    @Version
    private Long version = 0L;
}
