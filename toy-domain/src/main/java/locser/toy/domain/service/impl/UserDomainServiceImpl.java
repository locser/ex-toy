package locser.toy.domain.service.impl;

import locser.toy.domain.model.entity.User;
import locser.toy.domain.repository.UserRepository;
import locser.toy.domain.service.UserDomainService;
import org.springframework.stereotype.Service;

/**
 * Triển khai các dịch vụ miền cho Giveaway Campaign.
 */
@Service
public class UserDomainServiceImpl implements UserDomainService {

  private final UserRepository userRepository;

  public UserDomainServiceImpl(
      UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public User findUserByGoogleId(User user) {
    if (user.getGoogleId() == null) {
      return null;
    }

    User userExist = this.userRepository.findByGoogleId(user.getGoogleId());

    // create user
    if (userExist == null) {
      System.out.println(" 22222222222");
      userExist = this.userRepository.save(user);
    }

    System.out.println(" 333333333333333");

    return userExist;
  }
}