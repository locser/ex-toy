package locser.persistence.repository;

import locser.persistence.mapper.UserJPAMapper;
import locser.toy.domain.model.entity.User;
import locser.toy.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * Implementation of UserRepository using JPA.
 */
@Service
public class UserInfrasRepositoryImpl implements UserRepository {

  private static final long ALL_RECORDS = -1L;
  private final UserJPAMapper userJPAMapper;

  public UserInfrasRepositoryImpl(UserJPAMapper userJPAMapper) {
    this.userJPAMapper = userJPAMapper;
  }


  /**
   * Checks if all filters are present.
   *
   * @param userId     User ID filter
   * @param status     Status filter
   * @param campaignId Campaign ID filter
   * @return true if all filters are present
   */
  private boolean allFiltersPresent(Long userId, Integer status, Long campaignId) {
    return userId != null && status != null && campaignId != null;
  }

  @Override
  public User findById(Long id) {
    return null;
  }

  @Override
  public User save(User user) {

    return this.userJPAMapper.save(user);
  }

  @Override
  public User findByGoogleId(String googleId) {
    return null;
  }

  @Override
  public User findByEmail(String userIdentifier) {
    return this.userJPAMapper.findByEmail(userIdentifier);
  }
}