package club.rentstuff.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import club.rentstuff.entity.RentalItemEntity;
import club.rentstuff.entity.RentalItemImageEntity;
import club.rentstuff.entity.TaxonomyEntity;
import club.rentstuff.entity.UnavailableDateEntity;
import club.rentstuff.entity.UserEntity;
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

	public RentalItemDto getById(Long id) {
		RentalItemEntity entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
		return conversionService.convertRentalItemEntity(entity);
	}

	@Override
	public List<RentalItemDto> findByTaxonomyId(Long taxonomyId) {
		return repository.findByTaxonomyId(taxonomyId).stream().map(e -> conversionService.convertRentalItemEntity(e)).collect(Collectors.toList());
	}

	public RentalItemDto create(RentalItemDto dto) {
		
		    Optional<UserEntity> user = authService.getLoggedInUser();
		    if(user.isPresent()) {
		    	dto.setOwnerId(user.get().getId()); // Set ownerId from authenticated user
		    }
		
		RentalItemEntity entity = conversionService.convertRentalItemDto(dto);
		entity = repository.save(entity);
		return conversionService.convertRentalItemEntity(entity);
	}

	public RentalItemDto update(Long id, RentalItemDto dto) {

		RentalItemEntity entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));

		List<UnavailableDateEntity> unavailableDates = dto.getUnavailableDates().stream()
				.map(ud -> UnavailableDateEntity.builder().startDate(ud.getStartDate())
						.endDate(ud.getEndDate()).build())
				.collect(Collectors.toList());

		List<RentalItemImageEntity> images = entity.getImages();
		
		boolean modifiedImages = false;
		for(RentalItemImageEntity existingImage: images) {
			if(!dto.getImageUrls().contains(existingImage.getImageUrl())) {
				rentalItemImageRepo.delete(existingImage);
				modifiedImages = true;
			}
		}
		for(String updatedImage: dto.getImageUrls()) {
			if(!entity.getImages().stream().anyMatch(i -> i.getImageUrl().equals(updatedImage))) {
				rentalItemImageRepo.save(RentalItemImageEntity.builder().createDate(LocalDateTime.now()).modifyDate(LocalDateTime.now())
						.imageUrl(updatedImage).build());
				modifiedImages = true;
			}
		}
		List<RentalItemImageEntity> finalImages = images;
		if(modifiedImages) {
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
		entity.getUnavailableDates().addAll(unavailableDates);
		final RentalItemEntity finEnt = entity;
		unavailableDates.stream().forEach(u -> u.setRentalItem(finEnt.toBuilder().build()));
		entity = repository.save(entity);
		// TODO set updated prices per day intervals

		return conversionService.convertRentalItemEntity(entity);
	}

	@Override
	public List<RentalItemDto> getAll() {
		return repository.findAll().stream().map(e -> conversionService.convertRentalItemEntity(e)).collect(Collectors.toList());
	}


}