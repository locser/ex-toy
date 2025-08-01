package locser.toy.domain.repository;


import locser.toy.domain.model.entity.User;

public interface UserRepository {

  User findById(Long id);

  User save(User user);


  User findByGoogleId(String googleId);

  User findByEmail(String userIdentifier);
}