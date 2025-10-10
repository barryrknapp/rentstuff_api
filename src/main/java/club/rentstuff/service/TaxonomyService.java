package club.rentstuff.service;

import java.util.List;

import club.rentstuff.model.TaxonomyDto;

public interface TaxonomyService {

	TaxonomyDto createTaxonomy(String name, Long parentId);

	List<TaxonomyDto> getAll();

	List<TaxonomyDto> getTaxonomies(List<Long> taxonomyIds);

}
