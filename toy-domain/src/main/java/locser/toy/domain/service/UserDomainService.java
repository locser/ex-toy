package locser.toy.domain.service;

import locser.toy.domain.model.entity.User;

/**
 * Interface định nghĩa các dịch vụ miền cho Giveaway Campaign.
 */
public interface UserDomainService {


  User findUserByGoogleId(User user);
}