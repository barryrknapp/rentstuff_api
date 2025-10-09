package club.rentstuff.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import club.rentstuff.entity.ReviewUserEntity;

public interface ReviewUserRepo extends JpaRepository<ReviewUserEntity, Long> {

}
