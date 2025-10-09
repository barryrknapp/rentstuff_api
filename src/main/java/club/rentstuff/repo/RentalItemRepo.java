package club.rentstuff.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import club.rentstuff.entity.RentalItemEntity;

public interface RentalItemRepo extends JpaRepository<RentalItemEntity, Long> {

	List<RentalItemEntity> findByTaxonomiesId(Long taxonomyId);

}
