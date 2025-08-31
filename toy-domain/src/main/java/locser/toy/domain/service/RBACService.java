package locser.toy.domain.service;

import java.util.List;
import java.util.Set;

/**
 * Service interface for RBAC operations.
 */
public interface RBACService {
    
    /**
     * Check if user has specific permission.
     */
    boolean hasPermission(Long userId, String permissionName);
    
    /**
     * Get all permissions for user.
     */
    Set<String> getUserPermissions(Long userId);
    
    /**
     * Get all active roles for user.
     */
    List<String> getUserRoles(Long userId);
    
    /**
     * Assign role to user.
     */
    void assignRoleToUser(Long userId, String roleName, Long assignedBy);
    
    /**
     * Remove role from user.
     */
    void removeRoleFromUser(Long userId, String roleName);
    
    /**
     * Check if user has role.
     */
    boolean hasRole(Long userId, String roleName);
    
    /**
     * Check if user has any of the specified permissions.
     */
    boolean hasAnyPermission(Long userId, String... permissions);
    
    /**
     * Check if user has all of the specified permissions.
     */
    boolean hasAllPermissions(Long userId, String... permissions);
    
    /**
     * Check if user is owner of resource.
     */
    boolean isResourceOwner(Long userId, String resourceType, Long resourceId);
    
    /**
     * Clean up expired roles.
     */
    void cleanupExpiredRoles();
}