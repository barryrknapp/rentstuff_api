package club.rentstuff.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import club.rentstuff.entity.ReviewItemEntity;

public interface ReviewItemRepo extends JpaRepository<ReviewItemEntity, Long> {

	List<ReviewItemEntity> findByItemId(Long itemId);

	@Query("SELECT AVG(r.rating) FROM ReviewItemEntity r WHERE r.item.id = :itemId")
	Double findAverageRatingByItemId(@Param("itemId") Long itemId);
}
