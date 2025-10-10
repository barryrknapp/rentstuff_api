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
public class TaxonomyDto {

	private Long id;
	private String name;

	private Long parentId;

	private LocalDateTime createDate;

	private LocalDateTime modifyDate;
}