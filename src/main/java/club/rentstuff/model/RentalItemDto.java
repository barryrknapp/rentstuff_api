package club.rentstuff.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
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
	
	@Builder.Default
    private List<Long> imageIds = new ArrayList<>();
	private String description;
	
	@Builder.Default
	private List<PriceDto> prices = new ArrayList<>();
    private Integer minDays;
    private Integer maxDays;
	private LocalDateTime createDate;
	private LocalDateTime modifyDate;
	private Long ownerId;
    private Boolean paused;
    private String city; 
    private String state; 
    private String zipCode;
    private Double latitude;
    private Double longitude;

	@Builder.Default
	private List<Long> taxonomyIds = new ArrayList<>();
	private Double averageRating; // Optional: Computed from reviews
    
	@Builder.Default
	private List<UnavailableDateDto> unavailableDates = new ArrayList<>();
	private List<UnavailableDateDto> bookedDates = new ArrayList<>();
}
