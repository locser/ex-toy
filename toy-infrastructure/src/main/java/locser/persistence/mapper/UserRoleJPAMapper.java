package locser.persistence.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import locser.toy.domain.model.entity.UserRole;

/**
 * JPA Repository interface for UserRole entity.
 */
@Repository
public interface UserRoleJPAMapper extends JpaRepository<UserRole, Long>, JpaSpecificationExecutor<UserRole> {

  /**
   * Find active roles for user.
   *
   * @param userId The user ID
   * @return List of active user roles
   */
  @Query("SELECT ur FROM UserRole ur WHERE ur.userId = :userId AND ur.isActive = true AND (ur.expiresAt IS NULL OR ur.expiresAt > CURRENT_TIMESTAMP)")
  List<UserRole> findActiveByUserId(@Param("userId") Long userId);

  /**
   * Find users with specific role.
   *
   * @param roleId The role ID
   * @return List of active user roles for the role
   */
  @Query("SELECT ur FROM UserRole ur WHERE ur.roleId = :roleId AND ur.isActive = true AND (ur.expiresAt IS NULL OR ur.expiresAt > CURRENT_TIMESTAMP)")
  List<UserRole> findActiveByRoleId(@Param("roleId") Long roleId);

  /**
   * Find specific user-role mapping.
   *
   * @param userId The user ID
   * @param roleId The role ID
   * @return Optional UserRole mapping
   */
  Optional<UserRole> findByUserIdAndRoleId(Long userId, Long roleId);

  /**
   * Find expired role assignments.
   *
   * @param currentTime Current timestamp
   * @return List of expired user roles
   */
  @Query("SELECT ur FROM UserRole ur WHERE ur.isActive = true AND ur.expiresAt IS NOT NULL AND ur.expiresAt <= :currentTime")
  List<UserRole> findExpiredRoles(@Param("currentTime") LocalDateTime currentTime);

  /**
   * Check if user has role.
   *
   * @param userId   The user ID
   * @param roleId   The role ID
   * @param isActive Active status
   * @return true if mapping exists
   */
  boolean existsByUserIdAndRoleIdAndIsActive(Long userId, Long roleId, Boolean isActive);

  /**
   * Deactivate expired roles.
   *
   * @param currentTime Current timestamp
   * @return Number of updated records
   */
  @Modifying
  @Transactional
  @Query("UPDATE UserRole ur SET ur.isActive = false WHERE ur.isActive = true AND ur.expiresAt IS NOT NULL AND ur.expiresAt <= :currentTime")
  int deactivateExpiredRoles(@Param("currentTime") LocalDateTime currentTime);

}