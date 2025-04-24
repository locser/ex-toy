package locser.toy.domain.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity representing a Toy Photo in the toy exchange system.
 */
@Entity
@Table(name = "toy_photos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ToyPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Long id;

    @Column(name = "toy_id", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long toyId = 0L;

    @Column(name = "photo_url", nullable = false, columnDefinition = "VARCHAR(512) DEFAULT ''")
    private String photoUrl = "";

    @Column(name = "is_primary", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isPrimary = false;

    @Column(name = "uploaded_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @Version
    private Long version = 0L;
}
