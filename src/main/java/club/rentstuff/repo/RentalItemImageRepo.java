package club.rentstuff.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import club.rentstuff.entity.RentalItemImageEntity;

public interface RentalItemImageRepo extends JpaRepository<RentalItemImageEntity, Long> {

    @Query("SELECT r FROM RentalItemImageEntity r WHERE r.rentalItem.id = :itemId")
	List<RentalItemImageEntity> findByItemId(Long itemId);

}
