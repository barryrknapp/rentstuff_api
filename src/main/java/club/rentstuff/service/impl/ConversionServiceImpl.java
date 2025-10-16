package club.rentstuff.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import club.rentstuff.entity.BookingEntity;
import club.rentstuff.entity.PaymentEntity;
import club.rentstuff.entity.PriceEntity;
import club.rentstuff.entity.RentalItemEntity;
import club.rentstuff.entity.RentalItemImageEntity;
import club.rentstuff.entity.ReviewItemEntity;
import club.rentstuff.entity.ReviewUserEntity;
import club.rentstuff.entity.TaxonomyEntity;
import club.rentstuff.entity.UnavailableDateEntity;
import club.rentstuff.entity.UserEntity;
import club.rentstuff.model.BookingDto;
import club.rentstuff.model.PaymentDto;
import club.rentstuff.model.PriceDto;
import club.rentstuff.model.RentalItemDto;
import club.rentstuff.model.RentalItemImageDto;
import club.rentstuff.model.ReviewItemDto;
import club.rentstuff.model.ReviewUserDto;
import club.rentstuff.model.TaxonomyDto;
import club.rentstuff.model.UnavailableDateDto;
import club.rentstuff.model.UserDto;
import club.rentstuff.repo.TaxonomyRepo;
import club.rentstuff.repo.UserRepo;
import club.rentstuff.service.ConversionService;

@Service
public class ConversionServiceImpl implements ConversionService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private TaxonomyRepo taxonomyRepo;

	

	@Override
	public RentalItemImageDto convertRentalItemImageEntity(RentalItemImageEntity ent) {

		return RentalItemImageDto.builder().id(ent.getId()).createDate(ent.getCreateDate()).contentType(ent.getContentType())
				.modifyDate(ent.getModifyDate()).rentalItemId(ent.getRentalItem().getId()).build();
	}

	@Override
    public UnavailableDateDto convertUnavailableDateEntity(UnavailableDateEntity entity) {
        return UnavailableDateDto.builder()
            .id(entity.getId())
            .startDate(entity.getStartDate())
            .endDate(entity.getEndDate())
            .itemId(entity.getRentalItem().getId())
            .build();
    }
    
	@Override
	public RentalItemEntity convertRentalItemDto(RentalItemDto dto) {
		if (dto == null || dto.getOwnerId() == null) {
			throw new IllegalStateException("The owner of the rental item needs to be set!");
		}

		List<TaxonomyEntity> taxonomies = taxonomyRepo.findAllById(dto.getTaxonomyIds());
		UserEntity owner = userRepo.findById(dto.getOwnerId())
				.orElseThrow(() -> new IllegalStateException("User not found: " + dto.getOwnerId()));

		// Build RentalItemEntity without setting id (let database handle it)
		RentalItemEntity entity = RentalItemEntity.builder().name(dto.getName()).description(dto.getDescription())
				.minDays(dto.getMinDays()).maxDays(dto.getMaxDays()).owner(owner).taxonomies(taxonomies).paused(dto.getPaused())
				.city(dto.getCity()).state(dto.getState()).zipCode(dto.getZipCode())
				.createDate(LocalDateTime.now()).build();

		List<RentalItemImageEntity> images = dto.getImageIds().stream()
				.map(i -> RentalItemImageEntity.builder().modifyDate(LocalDateTime.now())
						.createDate(LocalDateTime.now()).id(i).rentalItem(entity).build())
				.collect(Collectors.toList());

		List<PriceEntity> prices = dto.getPrices().stream().map(p -> PriceEntity.builder().price(p.getPrice())
				.item(entity).minDaysRented(p.getMinDaysRented()).createDate(LocalDateTime.now()).build())
				.collect(Collectors.toList());

		// Build UnavailableDateEntity list
		List<UnavailableDateEntity> unavailableDates = dto
				.getUnavailableDates().stream().map(ud -> UnavailableDateEntity.builder().rentalItem(entity)
						.startDate(ud.getStartDate()).endDate(ud.getEndDate()).createDate(LocalDateTime.now()).build())
				.collect(Collectors.toList());

		entity.setUnavailableDates(unavailableDates);
		entity.setImages(images);
		entity.setPrices(prices);

		return entity;
	}

	@Override
	public RentalItemDto convertRentalItemEntity(RentalItemEntity e) {
		if (e == null) {
			return null;
		}

		// Compute average rating from reviews
		double averageRating = e.getReviews() != null && !e.getReviews().isEmpty()
				? e.getReviews().stream().mapToInt(ReviewItemEntity::getRating).average().orElse(0.0)
				: 0;

		// Extract taxonomy IDs
		List<Long> taxonomyIds = e.getTaxonomies().stream().map(TaxonomyEntity::getId).collect(Collectors.toList());

		return RentalItemDto.builder().id(e.getId()).name(e.getName()).description(e.getDescription()).paused(e.isPaused())
				.taxonomyIds(taxonomyIds)
				.prices(e.getPrices().stream().map(this::convertPriceEntity).collect(Collectors.toList()))
				.createDate(e.getCreateDate()).modifyDate(e.getModifyDate())
				.ownerId(e.getOwner() != null ? e.getOwner().getId() : null)
				.latitude(e.getLatitude()).longitude(e.getLongitude())
				.taxonomyIds(e.getTaxonomies().stream().map(t -> t.getId()).toList())
				.unavailableDates(e.getUnavailableDates().stream()
						.map(ud -> UnavailableDateDto.builder().id(ud.getId()).startDate(ud.getStartDate())
								.itemId(e.getId()).endDate(ud.getEndDate()).build())
						.collect(Collectors.toList()))
				.minDays(e.getMinDays()).maxDays(e.getMaxDays())
				.city(e.getCity()).state(e.getState()).zipCode(e.getZipCode())
				.imageIds(e.getImages().stream().map(i -> i.getId()).collect(Collectors.toList()))
				.averageRating(averageRating).build();

	}

	@Override
	public PaymentDto convertPaymentEntity(PaymentEntity e) {
		if (e == null) {
			return null;
		}
		return PaymentDto.builder().id(e.getId()).bookingId(e.getBooking() != null ? e.getBooking().getId() : null)
				.amount(e.getAmount()).status(e.getStatus() != null ? e.getStatus() : null)
				.paymentMethod(e.getPaymentMethod()).transactionId(e.getTransactionId()).createDate(e.getCreateDate())
				.modifyDate(e.getModifyDate()).build();
	}

	@Override
	public BookingDto convertBookingEntity(BookingEntity e) {
		if (e == null) {
			return null;
		}
		return BookingDto.builder().id(e.getId()).itemId(e.getItem() != null ? e.getItem().getId() : null)
				.userId(e.getRenter() != null ? e.getRenter().getId() : null).startDate(e.getStartDate())
				.endDate(e.getEndDate()).status(e.getStatus()).createDate(e.getCreateDate())
				.totalPrice(e.getTotalPrice())
				.modifyDate(e.getModifyDate()).build();
	}

	@Override
	public UserDto convertUserEntity(UserEntity e) {
		if (e == null) {
			return null;
		}
		return UserDto.builder().id(e.getId()).email(e.getEmail()).firstName(e.getFirstName()).lastName(e.getLastName())
				.role(e.getRole()).createDate(e.getCreateDate()).modifyDate(e.getModifyDate()).build();
	}

	@Override
	public ReviewUserDto convertReviewUserEntity(ReviewUserEntity e) {
		if (e == null) {
			return null;
		}
		return ReviewUserDto.builder().id(e.getId())
				.reviewedUserId(e.getReviewedUser() != null ? e.getReviewedUser().getId() : null)
				.reviewerUserId(e.getReviewer() != null ? e.getReviewer().getId() : null).rating(e.getRating())
				.comment(e.getComment()).createDate(e.getCreateDate()).modifyDate(e.getModifyDate()).build();
	}

	@Override
	public ReviewItemDto convertReviewItemEntity(ReviewItemEntity e) {
		if (e == null) {
			return null;
		}
		return ReviewItemDto.builder().id(e.getId()).itemId(e.getItem() != null ? e.getItem().getId() : null)
				.reviewerUserId(e.getReviewer() != null ? e.getReviewer().getId() : null).rating(e.getRating())
				.comment(e.getComment()).createDate(e.getCreateDate()).modifyDate(e.getModifyDate()).build();
	}

	@Override
	public TaxonomyDto convertTaxonomyEntity(TaxonomyEntity e) {
		if (e == null) {
			return null;
		}
		return TaxonomyDto.builder().id(e.getId()).name(e.getName())
				.parentId(e.getParent() != null ? e.getParent().getId() : null).createDate(e.getCreateDate())
				.modifyDate(e.getModifyDate()).build();
	}

	@Override
	public PriceDto convertPriceEntity(PriceEntity e) {
		if (e == null) {
			return null;
		}
		return PriceDto.builder().id(e.getId()).itemId(e.getItem() != null ? e.getItem().getId() : null)
				.price(e.getPrice()).minDaysRented(e.getMinDaysRented()).createDate(e.getCreateDate()).modifyDate(e.getModifyDate())
				.build();
	}

}