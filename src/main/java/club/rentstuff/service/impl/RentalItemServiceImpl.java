package club.rentstuff.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import club.rentstuff.entity.PriceEntity;
import club.rentstuff.entity.RentalItemEntity;
import club.rentstuff.entity.RentalItemImageEntity;
import club.rentstuff.entity.TaxonomyEntity;
import club.rentstuff.entity.UnavailableDateEntity;
import club.rentstuff.entity.UserEntity;
import club.rentstuff.model.PriceCalculationDto;
import club.rentstuff.model.RentalItemDto;
import club.rentstuff.repo.RentalItemImageRepo;
import club.rentstuff.repo.RentalItemRepo;
import club.rentstuff.repo.TaxonomyRepo;
import club.rentstuff.repo.UserRepo;
import club.rentstuff.service.AuthService;
import club.rentstuff.service.ConversionService;
import club.rentstuff.service.RentalItemService;

@Service
public class RentalItemServiceImpl implements RentalItemService {

	@Autowired
	private RentalItemRepo repository;

	@Autowired
	private ConversionService conversionService;

	@Autowired
	private TaxonomyRepo taxonomyRepo;

	@Autowired
	private AuthService authService;

	@Autowired
	private RentalItemImageRepo rentalItemImageRepo;

	@Autowired
	private UserRepo userRepo;

	/**
	 * calculates total price for a given number of days days*price
	 */
	@Override
	public PriceCalculationDto calculatePrice(Long itemId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
		RentalItemEntity item = repository.findById(itemId)
				.orElseThrow(() -> new RuntimeException("Item not found: " + itemId));

		// Validate dates
		if (startDateTime.isAfter(endDateTime)) {
			throw new RuntimeException("Dropoff date and time must be after pickup");
		}
		if (startDateTime.isBefore(LocalDateTime.now())) {
			throw new RuntimeException("Pickup date and time must be now or later");
		}

		// Calculate days (ceiling to account for partial days)
		long days = (long) Math.ceil(ChronoUnit.HOURS.between(startDateTime, endDateTime) / 24.0);
		if (days < item.getMinDays()) {
			throw new RuntimeException("Rental duration must be at least " + item.getMinDays() + " days");
		}
		if (days > item.getMaxDays()) {
			throw new RuntimeException("Rental duration cannot exceed " + item.getMaxDays() + " days");
		}

		// Validate against unavailable dates
		for (var unavailable : item.getUnavailableDates()) {
			LocalDateTime unavailableStart = unavailable.getStartDate().atStartOfDay();
			LocalDateTime unavailableEnd = unavailable.getEndDate().atStartOfDay().plusDays(1).minusMinutes(1);
			if (!startDateTime.isAfter(unavailableEnd) && !endDateTime.isBefore(unavailableStart)) {
				throw new RuntimeException("Selected dates overlap with unavailable dates");
			}
		}

		// Find applicable price (highest minDaysRented that is <= days)
		PriceEntity applicablePrice = item.getPrices().stream().filter(price -> price.getMinDaysRented() <= days)
				.max((p1, p2) -> Integer.compare(p1.getMinDaysRented(), p2.getMinDaysRented()))
				.orElseThrow(() -> new RuntimeException("No applicable price found for " + days + " days"));

		PriceCalculationDto result = new PriceCalculationDto();
		result.setTotalPrice(applicablePrice.getPrice().multiply(BigDecimal.valueOf(days)));
		result.setDays((int) days);
		return result;
	}

	public RentalItemDto getById(Long id) {
		RentalItemEntity entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
		return conversionService.convertRentalItemEntity(entity);
	}

	@Override
	public List<RentalItemDto> findByTaxonomyId(Long taxonomyId) {
		return repository.findByTaxonomyId(taxonomyId).stream().map(e -> conversionService.convertRentalItemEntity(e))
				.collect(Collectors.toList());
	}

	public RentalItemDto create(RentalItemDto dto) {

		Optional<UserEntity> user = authService.getLoggedInUser();
		if (user.isPresent()) {
			dto.setOwnerId(user.get().getId()); // Set ownerId from authenticated user
		}

		RentalItemEntity entity = conversionService.convertRentalItemDto(dto);
		entity = repository.save(entity);
		return conversionService.convertRentalItemEntity(entity);
	}

	public RentalItemDto update(Long id, RentalItemDto dto) {

		RentalItemEntity entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));

		List<UnavailableDateEntity> unavailableDates = dto.getUnavailableDates().stream().map(
				ud -> UnavailableDateEntity.builder().startDate(ud.getStartDate()).endDate(ud.getEndDate()).build())
				.collect(Collectors.toList());

		List<RentalItemImageEntity> images = entity.getImages();

		boolean modifiedImages = false;
		for (RentalItemImageEntity existingImage : images) {
			if (!dto.getImageUrls().contains(existingImage.getImageUrl())) {
				rentalItemImageRepo.delete(existingImage);
				modifiedImages = true;
			}
		}
		for (String updatedImage : dto.getImageUrls()) {
			if (!entity.getImages().stream().anyMatch(i -> i.getImageUrl().equals(updatedImage))) {
				rentalItemImageRepo.save(RentalItemImageEntity.builder().createDate(LocalDateTime.now())
						.modifyDate(LocalDateTime.now()).imageUrl(updatedImage).build());
				modifiedImages = true;
			}
		}
		List<RentalItemImageEntity> finalImages = images;
		if (modifiedImages) {
			finalImages = rentalItemImageRepo.findByItemId(entity.getId());
		}

		// these probably need to be ordered
		List<TaxonomyEntity> taxonomies = taxonomyRepo.findAllById(dto.getTaxonomyIds());
		UserEntity owner = userRepo.getById(dto.getOwnerId());

		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setMinDays(dto.getMinDays());
		entity.setMaxDays(dto.getMaxDays());
		entity.setImages(finalImages);
		entity.setTaxonomies(taxonomies);
		entity.setOwner(owner);
		entity.getUnavailableDates().clear();
		entity.setPaused(dto.getPaused());
		entity.getUnavailableDates().addAll(unavailableDates);
		final RentalItemEntity finEnt = entity;
		unavailableDates.stream().forEach(u -> u.setRentalItem(finEnt.toBuilder().build()));
		entity = repository.save(entity);
		// TODO set updated prices per day intervals

		return conversionService.convertRentalItemEntity(entity);
	}

	@Override
	public List<RentalItemDto> getAll() {
		return repository.findAll(Example.of(RentalItemEntity.builder().paused(false).build())).stream()
				.map(e -> conversionService.convertRentalItemEntity(e)).collect(Collectors.toList());
	}

	@Override
	public List<RentalItemDto> findByOwnerUserId(Long userId) {
		UserEntity owner = userRepo.findById(userId).get();
		List<RentalItemDto> activeItems = repository.findAll(Example.of(RentalItemEntity.builder().owner(owner).build())).stream()
				.map(e -> conversionService.convertRentalItemEntity(e)).collect(Collectors.toList());
		
		List<RentalItemDto> pausedItems = repository.findAll(Example.of(RentalItemEntity.builder().paused(true).owner(owner).build())).stream()
				.map(e -> conversionService.convertRentalItemEntity(e)).collect(Collectors.toList());
		
		List<RentalItemDto> allItems = new ArrayList<>();
		allItems.addAll(activeItems);
		allItems.addAll(pausedItems);
		return allItems;
		
	}

	@Override
	public List<RentalItemDto> findOwnedByUser() {
		UserEntity user = authService.getLoggedInUser().get();
		return findByOwnerUserId(user.getId());
	}

	@Override
	public RentalItemDto togglePause(Long itemId, boolean paused) {
		UserEntity user = authService.getLoggedInUser().get();
		RentalItemEntity item = repository.findById(itemId)
				.orElseThrow(() -> new IllegalArgumentException("Item not found"));

		if (!item.getOwner().getId().equals(user.getId())) {
			throw new IllegalStateException("Unauthorized to modify this item");
		}

		item.setPaused(paused);
		RentalItemEntity updatedItem = repository.save(item);
		return conversionService.convertRentalItemEntity(updatedItem);
	}

}