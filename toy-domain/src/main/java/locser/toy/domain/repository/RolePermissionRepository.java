package locser.toy.domain.repository;

import locser.toy.domain.model.entity.RolePermission;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for RolePermission entity operations.
 */
public interface RolePermissionRepository {
    
    /**
     * Find role permission by ID.
     */
    Optional<RolePermission> findById(Long id);
    
    /**
     * Find active permissions for role.
     */
    List<RolePermission> findActiveByRoleId(Long roleId);
    
    /**
     * Find roles with specific permission.
     */
    List<RolePermission> findActiveByPermissionId(Long permissionId);
    
    /**
     * Find specific role-permission mapping.
     */
    Optional<RolePermission> findByRoleIdAndPermissionId(Long roleId, Long permissionId);
    
    /**
     * Save role permission.
     */
    RolePermission save(RolePermission rolePermission);
    
    /**
     * Check if role has permission.
     */
    boolean existsByRoleIdAndPermissionIdAndIsActive(Long roleId, Long permissionId, Boolean isActive);
    
    /**
     * Find all role permissions.
     */
    List<RolePermission> findAll();
    
    /**
     * Delete role permission by ID.
     */
    void deleteById(Long id);
    
    /**
     * Find permissions by multiple role IDs.
     */
    List<RolePermission> findActiveByRoleIds(List<Long> roleIds);
}