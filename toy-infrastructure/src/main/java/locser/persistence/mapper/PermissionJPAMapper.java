package locser.persistence.mapper;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import locser.toy.domain.model.entity.Permission;

/**
 * JPA Repository interface for Permission entity.
 */
@Repository
public interface PermissionJPAMapper extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {

  /**
   * Find permission by name.
   *
   * @param name The permission name
   * @return Optional Permission
   */
  Optional<Permission> findByName(String name);

  /**
   * Find all active permissions.
   *
   * @return List of active permissions
   */
  @Query("SELECT p FROM Permission p WHERE p.isActive = true")
  List<Permission> findAllActive();

  /**
   * Find permissions by resource.
   *
   * @param resource The resource name
   * @return List of permissions for the resource
   */
  @Query("SELECT p FROM Permission p WHERE p.resource = :resource AND p.isActive = true")
  List<Permission> findByResource(@Param("resource") String resource);

  /**
   * Find permissions by resource and action.
   *
   * @param resource The resource name
   * @param action   The action name
   * @return List of permissions for the resource and action
   */
  @Query("SELECT p FROM Permission p WHERE p.resource = :resource AND p.action = :action AND p.isActive = true")
  List<Permission> findByResourceAndAction(@Param("resource") String resource, @Param("action") String action);

  /**
   * Check if permission exists by name.
   *
   * @param name The permission name
   * @return true if permission exists
   */
  boolean existsByName(String name);

  /**
   * Find permission by name and active status.
   *
   * @param name     The permission name
   * @param isActive Active status
   * @return Optional Permission
   */
  Optional<Permission> findByNameAndIsActive(String name, Boolean isActive);

}