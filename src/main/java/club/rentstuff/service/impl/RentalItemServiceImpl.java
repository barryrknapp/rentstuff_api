package club.rentstuff.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import club.rentstuff.entity.PriceEntity;
import club.rentstuff.entity.RentalItemEntity;
import club.rentstuff.entity.RentalItemImageEntity;
import club.rentstuff.entity.UnavailableDateEntity;
import club.rentstuff.entity.UserEntity;
import club.rentstuff.model.PriceCalculationDto;
import club.rentstuff.model.PriceDto;
import club.rentstuff.model.RentalItemDto;
import club.rentstuff.model.UnavailableDateDto;
import club.rentstuff.repo.PriceRepo;
import club.rentstuff.repo.RentalItemImageRepo;
import club.rentstuff.repo.RentalItemRepo;
import club.rentstuff.repo.TaxonomyRepo;
import club.rentstuff.repo.UnavailableDateRepo;
import club.rentstuff.repo.UserRepo;
import club.rentstuff.service.AuthService;
import club.rentstuff.service.ConversionService;
import club.rentstuff.service.RentalItemService;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class RentalItemServiceImpl implements RentalItemService {

	@Autowired
	private RentalItemRepo repository;

	@Autowired
	private PriceRepo priceRepo;

	@Autowired
	private UnavailableDateRepo unavailableDateRepo;

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
				.orElseThrow(() -> new IllegalStateException("Item not found: " + itemId));

		// Validate dates
		if (startDateTime.isAfter(endDateTime)) {
			throw new IllegalStateException("Dropoff date and time must be after pickup");
		}
		if (startDateTime.isBefore(LocalDateTime.now())) {
			throw new IllegalStateException("Pickup date and time must be now or later");
		}

		// Calculate days (ceiling to account for partial days)
		long days = (long) Math.ceil(ChronoUnit.HOURS.between(startDateTime, endDateTime) / 24.0);
		if (days < item.getMinDays()) {
			throw new IllegalStateException("Rental duration must be at least " + item.getMinDays() + " days");
		}
		if (days > item.getMaxDays()) {
			throw new IllegalStateException("Rental duration cannot exceed " + item.getMaxDays() + " days");
		}

		// Validate against unavailable dates
		for (var unavailable : item.getUnavailableDates()) {
			LocalDateTime unavailableStart = unavailable.getStartDate().atStartOfDay();
			LocalDateTime unavailableEnd = unavailable.getEndDate().atStartOfDay().plusDays(1).minusMinutes(1);
			if (!startDateTime.isAfter(unavailableEnd) && !endDateTime.isBefore(unavailableStart)) {
				throw new IllegalStateException("Selected dates overlap with unavailable dates");
			}
		}

		// Find applicable price (highest minDaysRented that is <= days)
		PriceEntity applicablePrice = item.getPrices().stream().filter(price -> price.getMinDaysRented() <= days)
				.max((p1, p2) -> Integer.compare(p1.getMinDaysRented(), p2.getMinDaysRented()))
				.orElseThrow(() -> new IllegalStateException("No applicable price found for " + days + " days"));

		PriceCalculationDto result = new PriceCalculationDto();
		result.setTotalPrice(applicablePrice.getPrice().multiply(BigDecimal.valueOf(days)));
		result.setDays((int) days);
		return result;
	}

	public RentalItemDto getById(Long id) {
		RentalItemEntity entity = repository.findById(id).orElseThrow(() -> new IllegalStateException("Item not found"));
		return conversionService.convertRentalItemEntity(entity);
	}

	@Override
	public List<RentalItemDto> findByTaxonomyId(Long taxonomyId) {
		return repository.findByTaxonomyId(taxonomyId).stream().map(e -> conversionService.convertRentalItemEntity(e))
				.collect(Collectors.toList());
	}

//	public RentalItemDto create(RentalItemDto dto) {
//
//		Optional<UserEntity> user = authService.getLoggedInUser();
//		if (user.isPresent()) {
//			dto.setOwnerId(user.get().getId()); // Set ownerId from authenticated user
//		}
//
//		RentalItemEntity entity = conversionService.convertRentalItemDto(dto);
//		entity = repository.save(entity);
//		return conversionService.convertRentalItemEntity(entity);
//	}

//	public RentalItemDto update(Long id, RentalItemDto dto) {
//
//		RentalItemEntity entity = repository.findById(id).orElseThrow(() -> new IllegalStateException("Item not found"));
//
//		List<UnavailableDateEntity> unavailableDates = dto.getUnavailableDates().stream().map(
//				ud -> UnavailableDateEntity.builder().startDate(ud.getStartDate()).endDate(ud.getEndDate()).build())
//				.collect(Collectors.toList());
//
//		List<RentalItemImageEntity> images = entity.getImages();
//
//		boolean modifiedImages = false;
//		for (RentalItemImageEntity existingImage : images) {
//			if (!dto.getImageUrls().contains(existingImage.getImageUrl())) {
//				rentalItemImageRepo.delete(existingImage);
//				modifiedImages = true;
//			}
//		}
//		for (String updatedImage : dto.getImageUrls()) {
//			if (!entity.getImages().stream().anyMatch(i -> i.getImageUrl().equals(updatedImage))) {
//				rentalItemImageRepo.save(RentalItemImageEntity.builder().createDate(LocalDateTime.now())
//						.modifyDate(LocalDateTime.now()).imageUrl(updatedImage).build());
//				modifiedImages = true;
//			}
//		}
//		List<RentalItemImageEntity> finalImages = images;
//		if (modifiedImages) {
//			finalImages = rentalItemImageRepo.findByItemId(entity.getId());
//		}
//
//		// these probably need to be ordered
//		List<TaxonomyEntity> taxonomies = taxonomyRepo.findAllById(dto.getTaxonomyIds());
//		UserEntity owner = userRepo.getById(dto.getOwnerId());
//
//		entity.setName(dto.getName());
//		entity.setDescription(dto.getDescription());
//		entity.setMinDays(dto.getMinDays());
//		entity.setMaxDays(dto.getMaxDays());
//		entity.setImages(finalImages);
//		entity.setTaxonomies(taxonomies);
//		entity.setOwner(owner);
//		entity.getUnavailableDates().clear();
//		entity.setPaused(dto.getPaused());
//		entity.getUnavailableDates().addAll(unavailableDates);
//		final RentalItemEntity finEnt = entity;
//		unavailableDates.stream().forEach(u -> u.setRentalItem(finEnt.toBuilder().build()));
//		entity = repository.save(entity);
//		// TODO set updated prices per day intervals
//
//		return conversionService.convertRentalItemEntity(entity);
//	}

	@Override
	public List<RentalItemDto> getAll() {
		return repository.findAll(Example.of(RentalItemEntity.builder().paused(false).build())).stream()
				.map(e -> conversionService.convertRentalItemEntity(e)).collect(Collectors.toList());
	}

	@Override
	public List<RentalItemDto> findByOwnerUserId(Long userId) {
		UserEntity owner = userRepo.findById(userId).get();
		List<RentalItemDto> activeItems = repository
				.findAll(Example.of(RentalItemEntity.builder().owner(owner).build())).stream()
				.map(e -> conversionService.convertRentalItemEntity(e)).collect(Collectors.toList());

		List<RentalItemDto> pausedItems = repository
				.findAll(Example.of(RentalItemEntity.builder().paused(true).owner(owner).build())).stream()
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

	@Override
	public RentalItemDto createRentalItemWithImages(RentalItemDto itemDto, List<MultipartFile> images) {

		Long userId = authService.getLoggedInUser().get().getId();

		itemDto.setOwnerId(userId);

		RentalItemEntity entity = conversionService.convertRentalItemDto(itemDto);

		RentalItemEntity savedItem = repository.save(entity);

		// Save images
		List<RentalItemImageEntity> imageEntities = new ArrayList<>();
		if (images != null && !images.isEmpty()) {
			for (MultipartFile image : images) {
				try {
					RentalItemImageEntity imageEntity = new RentalItemImageEntity();
					imageEntity.setRentalItem(savedItem);
					imageEntity.setImageData(image.getBytes());
					imageEntity.setContentType(image.getContentType());
					imageEntities.add(imageEntity);
				} catch (IOException e) {
					throw new IllegalStateException("Failed to process image: " + e.getMessage());
				}
			}
			rentalItemImageRepo.saveAll(imageEntities);
		}

		return conversionService.convertRentalItemEntity(savedItem);
	}

	@Override
	@Transactional
	public RentalItemDto updateRentalItemWithImages(RentalItemDto itemDto, List<MultipartFile> images) {
		log.debug("Updating rental item with ID: {}", itemDto.getId());

		RentalItemEntity entity = repository.findById(itemDto.getId())
				.orElseThrow(() -> new IllegalArgumentException("Item not found"));

		Long userId = authService.getLoggedInUser().get().getId();
		if (!entity.getOwner().getId().equals(userId)) {
			throw new IllegalStateException("Unauthorized to modify this item");
		}

		// Update scalar fields
		entity.setName(itemDto.getName());
		entity.setDescription(itemDto.getDescription());
		entity.setMinDays(itemDto.getMinDays());
		entity.setMaxDays(itemDto.getMaxDays());
		entity.setPaused(itemDto.getPaused());
		entity.setTaxonomies(taxonomyRepo.findAllById(itemDto.getTaxonomyIds()));

		// Add new images (existing images are managed via delete endpoint)
		List<RentalItemImageEntity> imageEntities = new ArrayList<>();
		if (images != null && !images.isEmpty()) {
			for (MultipartFile image : images) {
				try {
					if (!image.getContentType().startsWith("image/")) {
						throw new IllegalArgumentException("Only image files are allowed");
					}
					RentalItemImageEntity imageEntity = new RentalItemImageEntity();
					imageEntity.setRentalItem(entity);
					imageEntity.setImageData(image.getBytes());
					imageEntity.setContentType(image.getContentType());
					imageEntities.add(imageEntity);
				} catch (IOException e) {
					log.error("Failed to process image: {}", e.getMessage());
					throw new IllegalStateException("Failed to process image: " + e.getMessage());
				}
			}
			rentalItemImageRepo.saveAll(imageEntities);
		}

		RentalItemEntity updatedItem = repository.save(entity);
		log.debug("Updated item: {}", updatedItem);
		return conversionService.convertRentalItemEntity(updatedItem);
	}

	@Override
	@Transactional
	public UnavailableDateDto createUnavailableDate(UnavailableDateDto dateDto) {
		log.debug("Creating unavailable date for item ID: {}", dateDto.getItemId());
		RentalItemEntity item = repository.findById(dateDto.getItemId())
				.orElseThrow(() -> new IllegalArgumentException("Item not found"));

		Long userId = authService.getLoggedInUser().get().getId();
		if (!item.getOwner().getId().equals(userId)) {
			throw new IllegalStateException("Unauthorized to add unavailable date to this item");
		}

		UnavailableDateEntity date = UnavailableDateEntity.builder().rentalItem(item).startDate(dateDto.getStartDate())
				.endDate(dateDto.getEndDate()).createDate(LocalDateTime.now()).build();

		UnavailableDateEntity savedDate = unavailableDateRepo.save(date);
		return conversionService.convertUnavailableDateEntity(savedDate);
	}

	@Override
	@Transactional
	public PriceDto createPrice(PriceDto priceDto) {
		log.debug("Creating price for item ID: {}", priceDto.getItemId());
		RentalItemEntity item = repository.findById(priceDto.getItemId())
				.orElseThrow(() -> new IllegalArgumentException("Item not found"));

		Long userId = authService.getLoggedInUser().get().getId();
		if (!item.getOwner().getId().equals(userId)) {
			throw new IllegalStateException("Unauthorized to add price to this item");
		}

		PriceEntity price = PriceEntity.builder().price(priceDto.getPrice()).minDaysRented(priceDto.getMinDaysRented())
				.item(item).createDate(LocalDateTime.now()).build();

		PriceEntity savedPrice = priceRepo.save(price);
		return conversionService.convertPriceEntity(savedPrice);
	}

	@Override
	@Transactional
	public void deleteImage(Long imageId) {
		RentalItemImageEntity image = rentalItemImageRepo.findById(imageId)
				.orElseThrow(() -> new IllegalArgumentException("Image not found"));
		Long userId = authService.getLoggedInUser().get().getId();
		if (!image.getRentalItem().getOwner().getId().equals(userId)) {
			throw new IllegalStateException("Unauthorized to delete this image");
		}
		rentalItemImageRepo.deleteById(imageId);
	}

	@Override
	@Transactional
	public void deleteUnavailableDate(Long dateId) {
		UnavailableDateEntity date = unavailableDateRepo.findById(dateId)
				.orElseThrow(() -> new IllegalArgumentException("Unavailable date not found"));
		Long userId = authService.getLoggedInUser().get().getId();
		if (!date.getRentalItem().getOwner().getId().equals(userId)) {
			throw new IllegalStateException("Unauthorized to delete this unavailable date");
		}
		unavailableDateRepo.deleteById(dateId);
	}

	@Override
	@Transactional
	public void deletePrice(Long priceId) {
		PriceEntity price = priceRepo.findById(priceId)
				.orElseThrow(() -> new IllegalArgumentException("Price not found"));
		Long userId = authService.getLoggedInUser().get().getId();
		if (!price.getItem().getOwner().getId().equals(userId)) {
			throw new IllegalStateException("Unauthorized to delete this price");
		}
		List<PriceEntity> remainingPrices = priceRepo.findByItemId(price.getItem().getId());
		if (remainingPrices.size() <= 1) {
			throw new IllegalStateException("Cannot delete the last price; at least one price is required");
		}
		priceRepo.deleteById(priceId);
	}

	@Override
	public RentalItemImageEntity getImage(Long id) {
		return rentalItemImageRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Image not found"));
	}

}