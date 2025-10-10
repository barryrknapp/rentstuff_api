package club.rentstuff.api;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import club.rentstuff.entity.BookingEntity;
import club.rentstuff.model.BookingDto;
import club.rentstuff.service.BookingService;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    public static class BookingRequest {
        private Long itemId;
        private Long userId;
        private LocalDateTime startDate;
        private LocalDateTime endDate;

        // Getters and setters
        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public LocalDateTime getStartDate() { return startDate; }
        public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
        public LocalDateTime getEndDate() { return endDate; }
        public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    }

    @PreAuthorize("isAuthenticated()") 
    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestBody BookingRequest request) {
        BookingDto booking = bookingService.createBooking(
            request.getItemId(),
            request.getUserId(),
            request.getStartDate(),
            request.getEndDate()
        );
        return ResponseEntity.ok(booking);
    }
}