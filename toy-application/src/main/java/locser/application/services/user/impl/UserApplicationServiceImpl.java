package locser.application.services.user.impl;

import locser.application.services.user.UserApplicationService;
import locser.toy.domain.model.entity.User;
import locser.toy.domain.repository.UserRepository;
import locser.toy.domain.service.UserDomainService;
import org.springframework.stereotype.Service;

/**
 * Triển khai các dịch vụ ứng dụng cho Giveaway Campaign.
 */
@Service
public class UserApplicationServiceImpl implements UserApplicationService {

  private final UserDomainService userCampaignDomainService;
  private final UserRepository userRepository;

  public UserApplicationServiceImpl(
      UserDomainService userCampaignDomainService,
      UserRepository userRepository) {
    this.userCampaignDomainService = userCampaignDomainService;
    this.userRepository = userRepository;
  }

  @Override
  public User loginGoogle(User user) {

    User userGoogle = this.userCampaignDomainService.findUserByGoogleId(user);
    System.out.println("loginGoogle 1");
    // create token for user

    return (userGoogle);

  }

}