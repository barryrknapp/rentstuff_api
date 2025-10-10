package club.rentstuff.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import club.rentstuff.entity.PriceEntity;


public interface PriceRepo extends JpaRepository<PriceEntity, Long> {



	List<PriceEntity> findByItemId(Long itemId);


}
