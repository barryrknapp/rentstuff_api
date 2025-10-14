package club.rentstuff.service;

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

public interface ConversionService {

	RentalItemDto convertRentalItemEntity(RentalItemEntity e);

	PaymentDto convertPaymentEntity(PaymentEntity e);

	BookingDto convertBookingEntity(BookingEntity e);

	ReviewItemDto convertReviewItemEntity(ReviewItemEntity e);

	TaxonomyDto convertTaxonomyEntity(TaxonomyEntity e);

	PriceDto convertPriceEntity(PriceEntity e);

	ReviewUserDto convertReviewUserEntity(ReviewUserEntity e);

	UserDto convertUserEntity(UserEntity e);

	RentalItemEntity convertRentalItemDto(RentalItemDto dto);

	RentalItemImageDto convertRentalItemImageEntity(RentalItemImageEntity ent);

	UnavailableDateDto convertUnavailableDateEntity(UnavailableDateEntity savedDate);

}
