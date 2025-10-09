package club.rentstuff.service;

import club.rentstuff.entity.TaxonomyEntity;

public interface TaxonomyService {

	TaxonomyEntity createTaxonomy(String name, Long parentId);

}
