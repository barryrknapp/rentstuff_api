package club.rentstuff.model;

import java.time.LocalDateTime;
import java.util.List;

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
public class RentalItemDto {
	private Long id;
	private String name;
	private String imageUrl;
	private String description;
	private Double price;
	private LocalDateTime createDate;
	private LocalDateTime modifyDate;
	private Long ownerId;
	private List<Long> taxonomyIds; // For taxonomy hierarchy
	private Double averageRating; // Optional: Computed from reviews
}
