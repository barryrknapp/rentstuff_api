package club.rentstuff.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

import club.rentstuff.entity.BookingEntity;
import club.rentstuff.entity.PaymentEntity;
import club.rentstuff.repo.PaymentRepo;
import club.rentstuff.service.ConfigService;
import club.rentstuff.service.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepo paymentRepository;

    @Autowired
    private ConfigService configService;

    @Override
    public PaymentEntity initiatePayment(Long bookingId, Double amount) throws StripeException {
        // Set Stripe API key from config
        Stripe.apiKey = configService.getConfig("STRIPE_API_KEY");

        // Prepare parameters using legacy map-based API
        Map<String, Object> params = new HashMap<>();
        params.put("amount", (long) (amount * 100)); // Amount in cents
        params.put("currency", configService.getConfig("CURRENCY")); // e.g., "usd"
        params.put("payment_method_types", List.of("card")); // Supported methods

        // Create PaymentIntent using legacy API
        PaymentIntent intent = PaymentIntent.create(params);

        // Create PaymentEntity
        PaymentEntity payment = PaymentEntity.builder()
            .booking(BookingEntity.builder().id(bookingId).build())
            .amount(amount)
            .status(PaymentEntity.PaymentStatus.PROCESSING)
            .transactionId(intent.getId()) // Use intent.getId() directly
            .paymentMethod("CARD")
            .createDate(LocalDateTime.now())
            .build();

        return paymentRepository.save(payment);
    }
}