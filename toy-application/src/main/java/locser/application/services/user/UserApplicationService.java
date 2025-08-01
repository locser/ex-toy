package locser.application.services.user;

import locser.toy.domain.model.entity.User;

/**
 * Interface định nghĩa các dịch vụ ứng dụng cho Giveaway Campaign.
 */
public interface UserApplicationService {

  User loginGoogle(User user);
}