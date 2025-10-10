package club.rentstuff.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import club.rentstuff.entity.UserEntity;
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
public class BookingDto {
	private Long id;
	private Long itemId;
	private Long userId;
	private UserEntity renter;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private String status; // e.g., PENDING, CONFIRMED, CANCELLED

	private LocalDateTime createDate;
	private LocalDateTime modifyDate;
	@Builder.Default
	private List<PaymentDto> payments = new ArrayList<>();
}