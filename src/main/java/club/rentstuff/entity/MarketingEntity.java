package club.rentstuff.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "MARKETING")
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class MarketingEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "marketing_generator")
    @SequenceGenerator(name = "marketing_generator", sequenceName = "marketing_id_seq", allocationSize = 1)
    private Long id;

    
    
	@Column(name = "COMPANY_NAME")
	private String companyName;

	@Column(name = "DOMAIN")
	private String domain;

	@Column(name = "WEBSITE")
	private String website;

	@Column(name = "CATEGORY")
	private String category;

	@Column(name = "FOCUS_KEYWORD")
	private String focusKeyword;

	@Column(name = "X_API_KEY")
	private String xApiKey;

	@Column(name = "X_API_SECRET")
	private String xApiSecret;

	@Column(name = "X_ACCESS_TOKEN")
	private String xAccessToken;

	@Column(name = "X_ACCESS_TOKEN_SECRET")
	private String xAccessTokenSecret;

	@Column(name = "X_BEARER_TOKEN")
	private String xBearerToken;

}