package club.rentstuff.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import club.rentstuff.model.BookingDto;
import club.rentstuff.service.BookingService;

@RestController
@RequestMapping("/bookings")
public class BookingController {

	@Autowired
	private BookingService bookingService;

	@PreAuthorize("isAuthenticated()")
	@PostMapping
	public ResponseEntity<BookingDto> createBooking(@RequestBody BookingDto booking) {

		return ResponseEntity.ok(bookingService.createBooking(booking));
	}

	@GetMapping("/my-bookings")
	public ResponseEntity<List<BookingDto>> getBookingsByRenter() {
		List<BookingDto> items = bookingService.findBookingsForUser();
		return ResponseEntity.ok(items);
	}

	@PutMapping("/{id}")
	public ResponseEntity<BookingDto> updateBooking(@PathVariable Long id, @RequestBody BookingDto bookingRequest) {
		if (!id.equals(bookingRequest.getId())) {
			throw new IllegalArgumentException("Booking ID mismatch");
		}
		BookingDto updatedBooking = bookingService.updateBooking(bookingRequest);
		return ResponseEntity.ok(updatedBooking);
	}
	
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(
        @PathVariable Long id
    ) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/item/{itemId}")
    public List<BookingDto> getBookingsByItem(@PathVariable Long itemId) {
        return bookingService.findBookingsByItem(itemId);
    }

}