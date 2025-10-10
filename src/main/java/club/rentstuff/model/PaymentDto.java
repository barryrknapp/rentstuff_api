package club.rentstuff.model;

import java.time.LocalDateTime;

import club.rentstuff.model.PaymentIntentDto.PaymentIntentDtoBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class PaymentDto {
	private Long id;
	private Long bookingId;
	private Double amount;
	private PaymentStatus status;
	private String paymentMethod; // e.g., CREDIT_CARD, VENMO
	private String transactionId; // External payment system ID (e.g., Stripe)
	private LocalDateTime createDate;
	private LocalDateTime modifyDate;
}