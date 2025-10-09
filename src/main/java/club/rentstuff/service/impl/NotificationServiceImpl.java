package club.rentstuff.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import club.rentstuff.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendBookingConfirmation(Long bookingId, String userEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEmail);
        message.setSubject("Booking Confirmation");
        message.setText("Your booking #" + bookingId + " is confirmed!");
        mailSender.send(message);
    }
}
