package locser.persistence.mapper;

import java.util.List;
import java.util.Optional;
import locser.toy.domain.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository interface for User entity.
 */
@Repository
public interface UserJPAMapper extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

  /**
   * Find a toy by its ID.
   *
   * @param id The ID of the toy
   * @return An Optional containing the toy if found, or empty if not found
   */
  Optional<User> findOneById(Long id);

  List<User> findByStatus(Integer status);

  /**
   * Find toys by status with sorting.
   *
   * @param status The toy status
   * @param sort   Sort specification
   * @return List of toys with the given status
   */
  List<User> findByStatus(Integer status, Sort sort);

  /**
   * Find toys by status with pagination.
   *
   * @param status   The toy status
   * @param pageable Pagination information
   * @return Page of toys with the given status
   */
  Page<User> findByStatus(Integer status, Pageable pageable);

  /**
   * Find toys by user ID and status.
   *
   * @param userId The user ID
   * @param status The toy status
   * @return List of toys belonging to the user with the given status
   */
  List<User> findByIdAndStatus(Long userId, Integer status);

  /**
   * Find toys by user ID and status with sorting.
   *
   * @param userId The user ID
   * @param status The toy status
   * @param sort   Sort specification
   * @return List of toys belonging to the user with the given status
   */
  List<User> findByIdAndStatus(Long userId, Integer status, Sort sort);

  /**
   * Find toys by user ID and status with pagination.
   *
   * @param userId   The user ID
   * @param status   The toy status
   * @param pageable Pagination information
   * @return Page of toys belonging to the user with the given status
   */
  Page<User> findByIdAndStatus(Long userId, Integer status, Pageable pageable);

  /**
   * Find toys by list of IDs.
   *
   * @param ids List of toy IDs
   * @return List of toys with the given IDs
   */
  List<User> findByIdIn(List<Long> ids);

  User findByEmail(String userIdentifier);
}