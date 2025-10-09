package club.rentstuff.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import club.rentstuff.service.BookingService;

@SpringBootTest
public class BookingServiceTest {
	@Autowired
	private BookingService bookingService;

	private LocalDateTime now = LocalDateTime.now();

	@Test
	public void testBookingConflict() {
		assertThrows(IllegalStateException.class, () -> {
			bookingService.createBooking(1L, 1L, now, now);
		});
	}
}