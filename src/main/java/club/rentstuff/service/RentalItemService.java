package club.rentstuff.service;

import java.util.List;

import club.rentstuff.model.RentalItemDto;

public interface RentalItemService {

	RentalItemDto getById(Integer id);

	List<RentalItemDto> findByTaxonomyId(Long taxonomyId);

	RentalItemDto getItemForBooking(Long itemId);

}
