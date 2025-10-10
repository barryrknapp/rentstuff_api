package club.rentstuff.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import club.rentstuff.entity.ReviewUserEntity;

public interface ReviewUserRepo extends JpaRepository<ReviewUserEntity, Long> {

    @Query("SELECT AVG(r.rating) FROM ReviewUserEntity r WHERE r.reviewedUser.id = :reviewedUserId")
    Double findAverageRatingByReviewedUserId(@Param("reviewedUserId") Long reviewedUserId);

    @Query("SELECT r FROM ReviewUserEntity r WHERE r.reviewedUser.id = :id")
    List<ReviewUserEntity> findByReviewedUserId(@Param("id") Long id);

}
