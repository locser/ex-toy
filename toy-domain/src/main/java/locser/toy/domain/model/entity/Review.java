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
 * Entity representing a Review in the toy exchange system.
 */
@Entity
@Table(name = "reviews")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "exchange_id", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long exchangeId = 0L;

    @Column(name = "reviewer_id", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long reviewerId = 0L;

    @Column(name = "reviewed_user_id", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long reviewedUserId = 0L;

    @Column(name = "rating", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer rating = 0;

    @Column(name = "comment", nullable = false, columnDefinition = "TEXT")
    private String comment = "";

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Version
    private Long version = 0L;
}
