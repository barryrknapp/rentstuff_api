package club.rentstuff.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import club.rentstuff.entity.UserEntity;

public interface UserRepo extends JpaRepository<UserEntity, Long> {

	Optional<UserEntity> findByEmail(String email);

}
