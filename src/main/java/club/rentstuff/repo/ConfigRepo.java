package club.rentstuff.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import club.rentstuff.entity.ConfigEnt;

public interface ConfigRepo extends JpaRepository<ConfigEnt, Long> {
    Optional<ConfigEnt> findByKey(String key);
}