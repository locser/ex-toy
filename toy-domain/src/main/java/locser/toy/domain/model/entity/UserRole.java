package locser.toy.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * Entity representing the mapping between Users and Roles.
 * This is a separate table to manage many-to-many relationship without JPA relationships.
 */
@Entity
@Table(name = "user_roles", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"}))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRole extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "assigned_by", nullable = false)
    private Long assignedBy;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "assigned_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime assignedAt = LocalDateTime.now();

    public UserRole(Long userId, Long roleId, Long assignedBy) {
        this.userId = userId;
        this.roleId = roleId;
        this.assignedBy = assignedBy;
        this.isActive = true;
        this.assignedAt = LocalDateTime.now();
    }

    public UserRole(Long userId, Long roleId, Long assignedBy, LocalDateTime expiresAt) {
        this(userId, roleId, assignedBy);
        this.expiresAt = expiresAt;
    }
}