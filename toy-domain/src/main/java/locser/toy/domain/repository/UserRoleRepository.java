package locser.toy.domain.repository;

import locser.toy.domain.model.entity.UserRole;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for UserRole entity operations.
 */
public interface UserRoleRepository {
    
    /**
     * Find user role by ID.
     */
    Optional<UserRole> findById(Long id);
    
    /**
     * Find active roles for user.
     */
    List<UserRole> findActiveByUserId(Long userId);
    
    /**
     * Find users with specific role.
     */
    List<UserRole> findActiveByRoleId(Long roleId);
    
    /**
     * Find specific user-role mapping.
     */
    Optional<UserRole> findByUserIdAndRoleId(Long userId, Long roleId);
    
    /**
     * Find expired role assignments.
     */
    List<UserRole> findExpiredRoles(LocalDateTime currentTime);
    
    /**
     * Save user role.
     */
    UserRole save(UserRole userRole);
    
    /**
     * Check if user has role.
     */
    boolean existsByUserIdAndRoleIdAndIsActive(Long userId, Long roleId, Boolean isActive);
    
    /**
     * Find all user roles.
     */
    List<UserRole> findAll();
    
    /**
     * Delete user role by ID.
     */
    void deleteById(Long id);
    
    /**
     * Deactivate expired roles.
     */
    int deactivateExpiredRoles(LocalDateTime currentTime);
}