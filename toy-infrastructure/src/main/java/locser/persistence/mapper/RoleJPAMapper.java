package locser.persistence.mapper;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import locser.toy.domain.model.entity.Role;

/**
 * JPA Repository interface for Role entity.
 */
@Repository
public interface RoleJPAMapper extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

  /**
   * Find role by name.
   *
   * @param name The role name
   * @return Optional Role
   */
  Optional<Role> findByName(String name);

  /**
   * Find all active roles.
   *
   * @return List of active roles
   */
  @Query("SELECT r FROM Role r WHERE r.isActive = true")
  List<Role> findAllActive();

  /**
   * Check if role exists by name.
   *
   * @param name The role name
   * @return true if role exists
   */
  boolean existsByName(String name);

  /**
   * Find role by name and active status.
   *
   * @param name     The role name
   * @param isActive Active status
   * @return Optional Role
   */
  Optional<Role> findByNameAndIsActive(String name, Boolean isActive);

}