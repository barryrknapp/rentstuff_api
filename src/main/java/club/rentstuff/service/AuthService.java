package club.rentstuff.service;

import java.util.Optional;

import club.rentstuff.entity.UserEntity;

public interface AuthService {

	String login(String email, String password);

	Optional<UserEntity> getLoggedInUser();

}
