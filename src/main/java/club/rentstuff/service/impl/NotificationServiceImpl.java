package club.rentstuff.service.impl;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import club.rentstuff.entity.BookingEntity;
import club.rentstuff.entity.RentalItemEntity;
import club.rentstuff.entity.UserEntity;
import club.rentstuff.service.NotificationService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class NotificationServiceImpl implements NotificationService {
	@Autowired
	private JavaMailSender mailSender;

	private boolean useMailgun = true;
	private boolean isTest = false;
	
	@Autowired
	private RestTemplate restTemplate;

	@Value("${rentstuff.mailgun.apilkey}")
	private String apiKey;
	
	@Value("${rentstuff.mailgun.apilkey.sandbox}")
	private String apiKeySandbox;

	private final String mailgunUrlSandbox = "https://api.mailgun.net/v3/sandboxdf1122a80838417fbad5680e4ab02fd5.mailgun.org/messages";
	private final String mailgunUrl = "https://api.mailgun.net/v3/sandboxdf1122a80838417fbad5680e4ab02fd5.mailgun.org/messages";

	
	public JsonNode sendMessageWithMailGun(String to, String subject, String body) {
		
		// Set up headers with Basic Auth
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth("api", isTest? apiKeySandbox : apiKey);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		// Prepare form data
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("from", isTest ? "Mailgun Sandbox <postmaster@sandboxdf1122a80838417fbad5680e4ab02fd5.mailgun.org>" : "noreply@rentstuff.club");
		map.add("to", to);
		map.add("subject", subject);
		map.add("text", body);

		// Create HTTP entity
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

		// Send request and get response
		ResponseEntity<JsonNode> response = restTemplate.postForEntity(isTest ? mailgunUrlSandbox : mailgunUrl, request, JsonNode.class);

		return response.getBody();
	}

	@Override
	public void sendBookingConfirmationToRenter(BookingEntity savedBooking, UserEntity renter, RentalItemEntity item) {

		String subject = "RentStuff.club - Booking Confirmation";
		String body = getBodyForConfirmation(item, savedBooking);
		if (useMailgun) {
			try {
				sendMessageWithMailGun(renter.getEmail(), subject, body);
				return;
			} catch (Exception e) {
				log.error("Failed to send email with MailGun, falling back to gmail", e);
			}
		}

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(isTest ? "Barry R Knapp <barry25@hotmail.com>" : renter.getEmail());
		message.setSubject(subject);
		message.setText(body);
		mailSender.send(message);

	}

	@Override
	public void sendBookingConfirmationToOwner(BookingEntity savedBooking, UserEntity renter, RentalItemEntity item) {

		String subject = "RentStuff.club - Booking Confirmation";
		String body = getBodyForConfirmation(item, savedBooking) + " Confirm the booking and arrange payment with "
				+ renter.getFirstName() + " " + renter.getLastName() + " at " + renter.getEmail();

		if (useMailgun) {
			try {
				sendMessageWithMailGun(item.getOwner().getEmail(), subject, body);
				return;
			} catch (Exception e) {
				log.error("Failed to send email with MailGun, falling back to gmail", e);
			}
		}

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(item.getOwner().getEmail());
		message.setSubject("RentStuff.club - Booking Confirmation");
		message.setText(body);
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

		String subject = "RentStuff.club - Booking Canceled";
		String body = getBodyForDelete(booking);
		if (useMailgun) {
			try {
				sendMessageWithMailGun(booking.getRenter().getEmail(), subject, body);
				return;
			} catch (Exception e) {
				log.error("Failed to send email with MailGun, falling back to gmail", e);
			}
		}

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(booking.getRenter().getEmail());
		message.setSubject(subject);
		message.setText(body);
		mailSender.send(message);

	}

	@Override
	public void sendBookingDeletedToOwner(BookingEntity booking, UserEntity owner) {
		
		
		String subject = "RentStuff.club - Booking Canceled";
		String body = getBodyForDelete(booking);
		if (useMailgun) {
			try {
				sendMessageWithMailGun(owner.getEmail(), subject, body);
				return;
			} catch (Exception e) {
				log.error("Failed to send email with MailGun, falling back to gmail", e);
			}
		}

		
		
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(owner.getEmail());
		message.setSubject(subject);
		message.setText(getBodyForDelete(booking));
		mailSender.send(message);

	}

	private String getBodyForDelete(BookingEntity booking) {
		String start = booking.getStartDate().format(DateTimeFormatter.ISO_DATE) + " "
				+ booking.getStartDate().format(DateTimeFormatter.ISO_LOCAL_TIME);
		String end = booking.getEndDate().format(DateTimeFormatter.ISO_DATE) + " "
				+ booking.getEndDate().format(DateTimeFormatter.ISO_LOCAL_TIME);

		String text = booking.getItem().getName() + " has been CANCELED for rent on " + start + " to " + end
				+ " with booking #" + booking.getId() + ".";

		return text;

	}
}
