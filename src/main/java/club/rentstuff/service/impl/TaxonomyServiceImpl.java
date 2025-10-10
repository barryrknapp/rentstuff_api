package club.rentstuff.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import club.rentstuff.entity.TaxonomyEntity;
import club.rentstuff.model.TaxonomyDto;
import club.rentstuff.repo.TaxonomyRepo;
import club.rentstuff.service.ConfigService;
import club.rentstuff.service.ConversionService;
import club.rentstuff.service.TaxonomyService;

@Service
public class TaxonomyServiceImpl implements TaxonomyService {
	@Autowired
	private ConfigService configService;

	@Autowired
	private TaxonomyRepo taxonomyRepository;

	@Autowired
	private ConversionService conversionService;

	@Override
	public TaxonomyDto createTaxonomy(String name, Long parentId) {
		if (parentId != null) {
			int maxDepth = Integer.parseInt(configService.getConfig("MAX_TAXONOMY_DEPTH"));
			int depth = getTaxonomyDepth(parentId);
			if (depth >= maxDepth) {
				throw new IllegalStateException("Taxonomy depth exceeded");
			}
		}

		return null;
		// Create and save TaxonomyEntity
	}

	private int getTaxonomyDepth(Long taxonomyId) {
		int depth = 0;
		TaxonomyEntity current = taxonomyRepository.findById(taxonomyId).orElse(null);
		while (current != null) {
			depth++;
			current = current.getParent();
		}
		return depth;
	}

	@Override
	public List<TaxonomyDto> getTaxonomies(List<Long> taxonomyIds) {
		if (taxonomyIds.isEmpty()) {
			return getAll();
		}
		return taxonomyRepository.findAllById(taxonomyIds).stream().map(t -> conversionService.convertTaxonomyEntity(t))
				.collect(Collectors.toList());
	}

	@Override
	public List<TaxonomyDto> getAll() {
		return taxonomyRepository.findAll().stream().map(t -> conversionService.convertTaxonomyEntity(t))
				.collect(Collectors.toList());
	}

}