package club.rentstuff.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import club.rentstuff.entity.RentalItemEntity;

public interface RentalItemRepo extends JpaRepository<RentalItemEntity, Long> {


    @Query("SELECT ri FROM RentalItemEntity ri JOIN ri.taxonomies t WHERE t.id = :taxonomyId")
    List<RentalItemEntity> findByTaxonomyId(@Param("taxonomyId") Long taxonomyId);

}
