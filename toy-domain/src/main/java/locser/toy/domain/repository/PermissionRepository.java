package locser.toy.domain.repository;

import locser.toy.domain.model.entity.Permission;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Permission entity operations.
 */
public interface PermissionRepository {
    
    /**
     * Find permission by ID.
     */
    Optional<Permission> findById(Long id);
    
    /**
     * Find permission by name.
     */
    Optional<Permission> findByName(String name);
    
    /**
     * Find all active permissions.
     */
    List<Permission> findAllActive();
    
    /**
     * Find permissions by resource.
     */
    List<Permission> findByResource(String resource);
    
    /**
     * Find permissions by resource and action.
     */
    List<Permission> findByResourceAndAction(String resource, String action);
    
    /**
     * Save permission.
     */
    Permission save(Permission permission);
    
    /**
     * Check if permission exists by name.
     */
    boolean existsByName(String name);
    
    /**
     * Find all permissions.
     */
    List<Permission> findAll();
    
    /**
     * Delete permission by ID.
     */
    void deleteById(Long id);
}