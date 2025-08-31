package locser.toy.domain.repository;

import locser.toy.domain.model.entity.Role;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Role entity operations.
 */
public interface RoleRepository {
    
    /**
     * Find role by ID.
     */
    Optional<Role> findById(Long id);
    
    /**
     * Find role by name.
     */
    Optional<Role> findByName(String name);
    
    /**
     * Find all active roles.
     */
    List<Role> findAllActive();
    
    /**
     * Save role.
     */
    Role save(Role role);
    
    /**
     * Check if role exists by name.
     */
    boolean existsByName(String name);
    
    /**
     * Find all roles.
     */
    List<Role> findAll();
    
    /**
     * Delete role by ID.
     */
    void deleteById(Long id);
}