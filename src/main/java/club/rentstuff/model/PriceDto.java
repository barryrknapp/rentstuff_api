package club.rentstuff.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
public class PriceDto {
	private Long id;

	private Long itemId;
	private BigDecimal price;

	private Integer minDaysRented;

	private LocalDateTime createDate;

	private LocalDateTime modifyDate;
}