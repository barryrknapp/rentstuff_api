package club.rentstuff.service;

import java.time.LocalDateTime;
import java.util.List;

import club.rentstuff.entity.BookingEntity;
import club.rentstuff.model.BookingDto;

public interface BookingService {

	List<BookingDto> findAvailableItemsByTaxonomy(Long taxonomyId, LocalDateTime startDate, LocalDateTime endDate);

	List<BookingEntity> findOverlappingBookings(Long itemId, LocalDateTime startDate, LocalDateTime endDate);

	BookingDto createBooking(BookingDto booking);

	List<BookingDto> findByRenterUserId(Long userId);

	List<BookingDto> findBookingsForUser();

	BookingDto updateBooking(BookingDto bookingRequest);

	void deleteBooking(Long id);

	List<BookingDto> findBookingsByItem(Long itemId);

}
