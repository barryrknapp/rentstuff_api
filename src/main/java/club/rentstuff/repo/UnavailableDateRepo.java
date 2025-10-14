package club.rentstuff.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import club.rentstuff.entity.UnavailableDateEntity;

public interface UnavailableDateRepo extends JpaRepository<UnavailableDateEntity, Long> {



}
