package locser.persistence.mapper;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import locser.toy.domain.model.entity.RolePermission;

/**
 * JPA Repository interface for RolePermission entity.
 */
@Repository
public interface RolePermissionJPAMapper extends JpaRepository<RolePermission, Long>, JpaSpecificationExecutor<RolePermission> {

  /**
   * Find active permissions for role.
   *
   * @param roleId The role ID
   * @return List of active role permissions
   */
  @Query("SELECT rp FROM RolePermission rp WHERE rp.roleId = :roleId AND rp.isActive = true")
  List<RolePermission> findActiveByRoleId(@Param("roleId") Long roleId);

  /**
   * Find roles with specific permission.
   *
   * @param permissionId The permission ID
   * @return List of active role permissions for the permission
   */
  @Query("SELECT rp FROM RolePermission rp WHERE rp.permissionId = :permissionId AND rp.isActive = true")
  List<RolePermission> findActiveByPermissionId(@Param("permissionId") Long permissionId);

  /**
   * Find specific role-permission mapping.
   *
   * @param roleId       The role ID
   * @param permissionId The permission ID
   * @return Optional RolePermission mapping
   */
  Optional<RolePermission> findByRoleIdAndPermissionId(Long roleId, Long permissionId);

  /**
   * Check if role has permission.
   *
   * @param roleId       The role ID
   * @param permissionId The permission ID
   * @param isActive     Active status
   * @return true if mapping exists
   */
  boolean existsByRoleIdAndPermissionIdAndIsActive(Long roleId, Long permissionId, Boolean isActive);

  /**
   * Find permissions by multiple role IDs.
   *
   * @param roleIds List of role IDs
   * @return List of active role permissions
   */
  @Query("SELECT rp FROM RolePermission rp WHERE rp.roleId IN :roleIds AND rp.isActive = true")
  List<RolePermission> findActiveByRoleIds(@Param("roleIds") List<Long> roleIds);

}