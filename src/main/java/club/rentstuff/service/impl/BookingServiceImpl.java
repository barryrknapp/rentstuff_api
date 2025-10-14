package club.rentstuff.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import club.rentstuff.entity.BookingEntity;
import club.rentstuff.entity.RentalItemEntity;
import club.rentstuff.entity.UserEntity;
import club.rentstuff.model.BookingDto;
import club.rentstuff.repo.BookingRepo;
import club.rentstuff.repo.RentalItemRepo;
import club.rentstuff.repo.UserRepo;
import club.rentstuff.service.AuthService;
import club.rentstuff.service.BookingService;
import club.rentstuff.service.ConversionService;
import club.rentstuff.service.NotificationService;
import club.rentstuff.service.PaymentService;
import club.rentstuff.service.RentalItemService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepo bookingRepository;
    
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private ConversionService conversionService;

    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private AuthService authService;

    @Autowired
    private RentalItemService rentalItemService;
    
    @Autowired
    private RentalItemRepo itemRepo;
    
    @Override
    public BookingDto createBooking(BookingDto bookingRequest) {

    
        if(authService.getLoggedInUser().isEmpty()){
        	throw new IllegalArgumentException("User must be logged in. No Auth found for user");
        }
                
    	// Validate date range
        if (bookingRequest.getStartDate().isAfter(bookingRequest.getEndDate()) || bookingRequest.getStartDate().isEqual(bookingRequest.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        
        
        RentalItemEntity item = itemRepo.getById(bookingRequest.getItemId());
        
        if (item.isPaused()) {
            throw new IllegalStateException("Cannot book a paused item");
        }
    	

        // Check for overlapping bookings
        List<BookingEntity> conflicts = bookingRepository.findOverlappingBookings(bookingRequest.getItemId(), bookingRequest.getStartDate(), bookingRequest.getEndDate());
        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("Item is already booked for the selected dates");
        }

        BigDecimal totalPrice = rentalItemService.calculatePrice(bookingRequest.getItemId(), bookingRequest.getStartDate(), bookingRequest.getEndDate()).getTotalPrice();       

        UserEntity renter = userRepo.getById(authService.getLoggedInUser().get().getId());
        
        // Create booking
        BookingEntity booking = BookingEntity.builder()
            .item(RentalItemEntity.builder().id(bookingRequest.getItemId()).build())
            .renter(renter)
            .startDate(bookingRequest.getStartDate())
            .endDate(bookingRequest.getEndDate())
            .totalPrice(totalPrice)
            .status("PENDING")
            .createDate(LocalDateTime.now())
            .build();

        BookingEntity savedBooking = bookingRepository.save(booking);

        
//        try {
        	//TODO
 //           PaymentEntity payment = paymentService.initiatePayment(savedBooking.getId(), totalPrice);

//        } catch (StripeException e) {
//           throw new RuntimeException("Failed to initiate payment", e);
//        }
        
        try {
        	notificationService.sendBookingConfirmationToRenter(savedBooking, renter, item);
        }catch(Exception e) {
        	log.error("Failed to notify renter but booking was saved " + savedBooking);
        }

        try {
        	notificationService.sendBookingConfirmationToOwner(savedBooking, renter, item );
        }catch(Exception e) {
        	log.error("Failed to notify owner but booking was saved " + savedBooking);
        }
        return conversionService.convertBookingEntity(savedBooking);
    }
	@Override
    public List<BookingEntity> findOverlappingBookings(Long itemId, LocalDateTime startDate, LocalDateTime endDate) {
        return bookingRepository.findOverlappingBookings(itemId, startDate, endDate);
    }

	@Override
	public List<BookingDto> findAvailableItemsByTaxonomy(Long taxonomyId, LocalDateTime startDate,
			LocalDateTime endDate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<BookingDto> findByRenterUserId(Long userId) {
		UserEntity renter = userRepo.findById(userId).get();
		List<BookingEntity> bookings = bookingRepository.findAll(Example.of(BookingEntity.builder().renter(renter).build()));
		return  bookings.stream().map(e -> conversionService.convertBookingEntity(e)).collect(Collectors.toList());
		
		
	}
	@Override
	public List<BookingDto> findBookingsForUser() {
		UserEntity renter = authService.getLoggedInUser().get();
		List<BookingEntity> bookings = bookingRepository.findAll(Example.of(BookingEntity.builder().renter(renter).build()));
		return  bookings.stream().map(e -> conversionService.convertBookingEntity(e)).collect(Collectors.toList());
	}
	
	@Override
    public BookingDto updateBooking(BookingDto bookingRequest) {
		
		UserEntity user = authService.getLoggedInUser().get();
		
        BookingEntity booking = bookingRepository.findById(bookingRequest.getId())
            .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        

        if (!booking.getRenter().getId().equals(user.getId())) {
            throw new IllegalStateException("Unauthorized to edit this booking");
        }

        // Validate status
        if (!List.of("PENDING", "CONFIRMED").contains(booking.getStatus())) {
            throw new IllegalStateException("Cannot edit booking with status: " + booking.getStatus());
        }

        // Validate date range
        if (bookingRequest.getStartDate().isAfter(bookingRequest.getEndDate()) ||
            bookingRequest.getStartDate().isEqual(bookingRequest.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        // Check for overlapping bookings
        List<BookingEntity> conflicts = bookingRepository.findOverlappingBookings(
            bookingRequest.getItemId(), 
            bookingRequest.getStartDate(), 
            bookingRequest.getEndDate()
        );
        conflicts.removeIf(b -> b.getId().equals(booking.getId())); // Exclude current booking
        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("Item is already booked for the selected dates");
        }

        // Update fields
        booking.setStartDate(bookingRequest.getStartDate());
        booking.setEndDate(bookingRequest.getEndDate());
        booking.setStatus("PENDING"); // Reset to PENDING
        booking.setTotalPrice(rentalItemService.calculatePrice(
            bookingRequest.getItemId(), 
            bookingRequest.getStartDate(), 
            bookingRequest.getEndDate()
        ).getTotalPrice());
        booking.setModifyDate(LocalDateTime.now());

        BookingEntity updatedBooking = bookingRepository.save(booking);
        return conversionService.convertBookingEntity(updatedBooking);
    }
	
	@Override
    public void deleteBooking(Long id) {
		
		UserEntity user = authService.getLoggedInUser().get();
        BookingEntity booking = bookingRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        
        UserEntity owner = booking.getItem().getOwner();
        
        // Verify user owns the booking
        if (!owner.getId().equals(user.getId()) && !booking.getRenter().getId().equals(user.getId())) {
            throw new IllegalStateException("Unauthorized to delete this booking");
        }

        // Validate status
        if (!"PENDING".equals(booking.getStatus())) {
            throw new IllegalStateException("Cannot delete booking with status: " + booking.getStatus());
        }

        
        bookingRepository.delete(booking);
        
        
        notificationService.sendBookingDeletedToRenter(booking, owner);
        notificationService.sendBookingDeletedToOwner(booking, owner);
        
    }
	
	
	@Override
    public List<BookingDto> findBookingsByItem(Long itemId) {
        List<BookingEntity> entities = bookingRepository.findByItemId(itemId);
        return entities.stream()
            .map(conversionService::convertBookingEntity)
            .collect(Collectors.toList());
    }

}