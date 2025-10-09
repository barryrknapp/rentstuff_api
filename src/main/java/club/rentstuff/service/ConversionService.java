package club.rentstuff.service;

import club.rentstuff.entity.RentalItemEntity;
import club.rentstuff.model.RentalItemDto;

public interface ConversionService {

	RentalItemDto convert(RentalItemEntity e);

}
