package club.rentstuff.service;

import java.time.LocalDateTime;
import java.util.List;

import club.rentstuff.entity.BookingEntity;

public interface BookingService {

	List<BookingEntity> findAvailableItemsByTaxonomy(Long taxonomyId, LocalDateTime startDate, LocalDateTime endDate);

	BookingEntity createBooking(Long itemId, Long userId, LocalDateTime startDate, LocalDateTime endDate);

	List<BookingEntity> findOverlappingBookings(Long itemId, LocalDateTime startDate, LocalDateTime endDate);

}
