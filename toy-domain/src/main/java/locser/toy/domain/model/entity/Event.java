package locser.toy.domain.model.entity;

import jakarta.persistence.*;
import locser.toy.domain.model.enums.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity đại diện cho Sự kiện trong hệ thống trao đổi đồ chơi.
 * Sự kiện là các chiến dịch mà người dùng có thể trao đổi đồ chơi trong một khoảng thời gian cụ thể.
 * Lưu trữ thông tin về tên, mô tả, thời gian bắt đầu/kết thúc, chủ đề và các quy tắc của sự kiện.
 */
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

    @Column(name = "description", nullable = false, columnDefinition = "INT DEFAULT 0")
    private String description;

    @Column(name = "start_date", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false , columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
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