package club.rentstuff.service;

public interface NotificationService {

	void sendBookingConfirmation(Long bookingId, String userEmail);

}
