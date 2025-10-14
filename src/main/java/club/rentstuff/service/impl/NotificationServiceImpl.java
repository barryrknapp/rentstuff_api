package club.rentstuff.service.impl;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import club.rentstuff.entity.BookingEntity;
import club.rentstuff.entity.RentalItemEntity;
import club.rentstuff.entity.UserEntity;
import club.rentstuff.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {
	@Autowired
	private JavaMailSender mailSender;

	@Override
	public void sendBookingConfirmationToRenter(BookingEntity savedBooking, UserEntity renter, RentalItemEntity item) {

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(renter.getEmail());
		message.setSubject("RentStuff.club - Booking Confirmation");
		message.setText(getBodyForConfirmation(item, savedBooking));
		mailSender.send(message);

	}

	@Override
	public void sendBookingConfirmationToOwner(BookingEntity savedBooking, UserEntity renter, RentalItemEntity item) {

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(item.getOwner().getEmail());
		message.setSubject("RentStuff.club - Booking Confirmation");
		message.setText(getBodyForConfirmation(item, savedBooking) + " Confirm the booking and arrange payment with "
				+ renter.getFirstName() + " " + renter.getLastName() + " at " + renter.getEmail());
		mailSender.send(message);

	}

	private String getBodyForConfirmation(RentalItemEntity item, BookingEntity savedBooking) {
		String start = savedBooking.getStartDate().format(DateTimeFormatter.ISO_DATE) + " "
				+ savedBooking.getStartDate().format(DateTimeFormatter.ISO_LOCAL_TIME);
		String end = savedBooking.getEndDate().format(DateTimeFormatter.ISO_DATE) + " "
				+ savedBooking.getEndDate().format(DateTimeFormatter.ISO_LOCAL_TIME);

		String text = item.getName() + " has been requested for rent on " + start + " to " + end + " with booking #"
				+ savedBooking.getId() + ".  View Booking online at https://www.rentstuff.club";

		return text;

	}

	@Override
	public void sendBookingDeletedToRenter(BookingEntity booking, UserEntity owner) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(booking.getRenter().getEmail());
		message.setSubject("RentStuff.club - Booking Canceled");
		message.setText(getBodyForDelete(booking));
		mailSender.send(message);
		
	}

	@Override
	public void sendBookingDeletedToOwner(BookingEntity booking, UserEntity owner) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(owner.getEmail());
		message.setSubject("RentStuff.club - Booking Canceled");
		message.setText(getBodyForDelete(booking));
		mailSender.send(message);
		
	}
	
	private String getBodyForDelete(BookingEntity booking) {
		String start = booking.getStartDate().format(DateTimeFormatter.ISO_DATE) + " "
				+ booking.getStartDate().format(DateTimeFormatter.ISO_LOCAL_TIME);
		String end = booking.getEndDate().format(DateTimeFormatter.ISO_DATE) + " "
				+ booking.getEndDate().format(DateTimeFormatter.ISO_LOCAL_TIME);

		String text = booking.getItem().getName() + " has been CANCELED for rent on " + start + " to " + end + " with booking #"
				+ booking.getId() + ".";

		return text;

	}
}
