package club.rentstuff.service;

import club.rentstuff.entity.BookingEntity;
import club.rentstuff.entity.RentalItemEntity;
import club.rentstuff.entity.UserEntity;

public interface NotificationService {


	void sendBookingConfirmationToRenter(BookingEntity savedBooking, UserEntity renter, RentalItemEntity item);

	void sendBookingConfirmationToOwner(BookingEntity savedBooking, UserEntity renter, RentalItemEntity item);

	void sendBookingDeletedToRenter(BookingEntity booking, UserEntity owner);

	void sendBookingDeletedToOwner(BookingEntity booking, UserEntity owner);

}
