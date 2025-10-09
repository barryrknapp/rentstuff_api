package club.rentstuff.service;

import com.stripe.exception.StripeException;

import club.rentstuff.entity.PaymentEntity;

public interface PaymentService {

	PaymentEntity initiatePayment(Long bookingId, Double amount) throws StripeException;

}
