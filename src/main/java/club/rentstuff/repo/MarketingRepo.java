package club.rentstuff.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import club.rentstuff.entity.MarketingEntity;

public interface MarketingRepo extends JpaRepository<MarketingEntity, Long> {

}
