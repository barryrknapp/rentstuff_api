package club.rentstuff.model;

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
public class RentalItemImageDto {
	private Long id;
	private Long rentalItemId;

	private String imageUrl;

	private LocalDateTime createDate;

	private LocalDateTime modifyDate;
}