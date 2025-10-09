package club.rentstuff.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import club.rentstuff.entity.TaxonomyEntity;

public interface TaxonomyRepo extends JpaRepository<TaxonomyEntity, Long> {
    List<TaxonomyEntity> findByParentId(Long parentId);
}