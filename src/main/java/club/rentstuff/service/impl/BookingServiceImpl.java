package club.rentstuff.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;

import club.rentstuff.entity.BookingEntity;
import club.rentstuff.entity.PaymentEntity;
import club.rentstuff.entity.RentalItemEntity;
import club.rentstuff.entity.UserEntity;
import club.rentstuff.repo.BookingRepo;
import club.rentstuff.repo.RentalItemRepo;
import club.rentstuff.service.BookingService;
import club.rentstuff.service.NotificationService;
import club.rentstuff.service.PaymentService;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepo bookingRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private RentalItemRepo itemRepo;
    
    @Override
    public BookingEntity createBooking(Long itemId, Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        // Validate date range
        if (startDate.isAfter(endDate) || startDate.isEqual(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        // Check for overlapping bookings
        List<BookingEntity> conflicts = bookingRepository.findOverlappingBookings(itemId, startDate, endDate);
        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("Item is already booked for the selected dates");
        }

        RentalItemEntity item = itemRepo.getReferenceById(itemId);
        
        // Create booking
        BookingEntity booking = BookingEntity.builder()
            .item(RentalItemEntity.builder().id(itemId).build())
            .renter(UserEntity.builder().id(userId).build())
            .startDate(startDate)
            .endDate(endDate)
            .status("PENDING")
            .createDate(LocalDateTime.now())
            .build();

        BookingEntity savedBooking = bookingRepository.save(booking);

        
        
        
        try {
            PaymentEntity payment = paymentService.initiatePayment(savedBooking.getId(), item.getPrice());
            // Optionally link payment to booking
        } catch (StripeException e) {
            throw new RuntimeException("Failed to initiate payment", e);
        }
        
        notificationService.sendBookingConfirmation(savedBooking.getId(), savedBooking.getRenter().getEmail());
        return savedBooking;
    }

    @Override
    public List<BookingEntity> findOverlappingBookings(Long itemId, LocalDateTime startDate, LocalDateTime endDate) {
        return bookingRepository.findOverlappingBookings(itemId, startDate, endDate);
    }

	@Override
	public List<BookingEntity> findAvailableItemsByTaxonomy(Long taxonomyId, LocalDateTime startDate,
			LocalDateTime endDate) {
		// TODO Auto-generated method stub
		return null;
	}


}