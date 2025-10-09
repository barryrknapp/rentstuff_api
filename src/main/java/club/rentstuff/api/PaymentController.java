package club.rentstuff.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.StripeException;

import club.rentstuff.entity.PaymentEntity;
import club.rentstuff.service.PaymentService;

@RestController
@RequestMapping("/secure/payments")
public class PaymentController {

	@Autowired
	private PaymentService paymentService;

	public static class PaymentRequest {
		private Long bookingId;
		private Double amount;

		// Getters and setters
		public Long getBookingId() {
			return bookingId;
		}

		public void setBookingId(Long bookingId) {
			this.bookingId = bookingId;
		}

		public Double getAmount() {
			return amount;
		}

		public void setAmount(Double amount) {
			this.amount = amount;
		}
	}

	@PostMapping
	public ResponseEntity<PaymentEntity> initiatePayment(@RequestBody PaymentRequest request) {
		try {
			PaymentEntity payment = paymentService.initiatePayment(request.getBookingId(), request.getAmount());
			return ResponseEntity.ok(payment);
		} catch (StripeException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}
}