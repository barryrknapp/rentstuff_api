package club.rentstuff.service;

import java.util.List;

import club.rentstuff.model.RentalItemDto;

public interface RentalItemService {

	RentalItemDto getById(Long id);

	List<RentalItemDto> findByTaxonomyId(Long taxonomyId);

	RentalItemDto update(Long id, RentalItemDto dto);

	RentalItemDto create(RentalItemDto dto);

	List<RentalItemDto> getAll();

}
