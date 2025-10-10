package club.rentstuff.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.stripe.exception.StripeException;

import club.rentstuff.entity.PaymentEntity;
import club.rentstuff.model.PaymentStatus;
import club.rentstuff.service.ConfigService;
import club.rentstuff.service.PaymentService;

@SpringBootTest
public class PaymentServiceTest {
	@Autowired
	private PaymentService paymentService;
	@MockBean
	private ConfigService configService;

	@Test
	public void testInitiatePayment() throws StripeException {
		when(configService.getConfig("STRIPE_API_KEY")).thenReturn("sk_test_...");
		when(configService.getConfig("CURRENCY")).thenReturn("usd");

		PaymentEntity payment = paymentService.initiatePayment(1L, 50.0);
		assertEquals(PaymentStatus.PROCESSING, payment.getStatus());
		assertNotNull(payment.getTransactionId());
	}
}
