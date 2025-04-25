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
    @Column(name = "id")
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
