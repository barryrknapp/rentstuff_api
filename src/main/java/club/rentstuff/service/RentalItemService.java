package club.rentstuff.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import club.rentstuff.entity.RentalItemImageEntity;
import club.rentstuff.model.PriceCalculationDto;
import club.rentstuff.model.PriceDto;
import club.rentstuff.model.RentalItemDto;
import club.rentstuff.model.UnavailableDateDto;
import jakarta.validation.Valid;

public interface RentalItemService {

	RentalItemDto getById(Long id);

	List<RentalItemDto> findByTaxonomyId(Long taxonomyId);

//	RentalItemDto update(Long id, RentalItemDto dto);

//	RentalItemDto create(RentalItemDto dto);

	List<RentalItemDto> getAll();

	PriceCalculationDto calculatePrice(Long itemId, LocalDateTime startDateTime, LocalDateTime endDateTime);

	List<RentalItemDto> findByOwnerUserId(Long userId);

	List<RentalItemDto> findOwnedByUser();

	RentalItemDto togglePause(Long itemId, boolean paused);

	RentalItemImageEntity getImage(Long id);

	RentalItemDto updateRentalItemWithImages(@Valid RentalItemDto itemDto, List<MultipartFile> images);

	RentalItemDto createRentalItemWithImages(@Valid RentalItemDto itemDto, List<MultipartFile> images);

	void deletePrice(Long priceId);

	void deleteUnavailableDate(Long dateId);

	void deleteImage(Long imageId);

	PriceDto createPrice(@Valid PriceDto priceDto);

	UnavailableDateDto createUnavailableDate(@Valid UnavailableDateDto dateDto);


}
