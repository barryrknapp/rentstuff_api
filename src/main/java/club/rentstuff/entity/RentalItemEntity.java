package club.rentstuff.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
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
@Table(name = "RENTAL_ITEM")
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class RentalItemEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rental_item_generator")
    @SequenceGenerator(name = "rental_item_generator", sequenceName = "rental_item_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;
   

    @Column(name = "CREATE_DATE")
    private LocalDateTime createDate;

    @Column(name = "MODIFY_DATE")
    private LocalDateTime modifyDate;

    @Column(name = "MIN_DAYS")
    private Integer minDays;
    
    @Column(name = "MAX_DAYS")
    private Integer maxDays;
    
    
    @ManyToOne
    @JoinColumn(name = "OWNER_ID", nullable = false)
    private UserEntity owner;

    @OneToMany(mappedBy = "rentalItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RentalItemImageEntity> images = new ArrayList<>();
    
    @OneToMany(mappedBy = "rentalItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UnavailableDateEntity> unavailableDates = new ArrayList<>();
    
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookingEntity> bookings = new ArrayList<>();
    
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReviewItemEntity> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PriceEntity> prices = new ArrayList<>();
    
    @ManyToMany
    @JoinTable(
        name = "RENTAL_ITEM_TAXONOMY",
        joinColumns = @JoinColumn(name = "ITEM_ID"),
        inverseJoinColumns = @JoinColumn(name = "TAXONOMY_ID")
    )
    @OrderBy("name ASC")
    private List<TaxonomyEntity> taxonomies = new ArrayList<>();
    
    
    

    // Ensure lists are never null
    public List<RentalItemImageEntity> getImages() {
        if (images == null) {
            images = new ArrayList<>();
        }
        return images;
    }

    public List<UnavailableDateEntity> getUnavailableDates() {
        if (unavailableDates == null) {
            unavailableDates = new ArrayList<>();
        }
        return unavailableDates;
    }

    public List<BookingEntity> getBookings() {
        if (bookings == null) {
            bookings = new ArrayList<>();
        }
        return bookings;
    }

    public List<ReviewItemEntity> getReviews() {
        if (reviews == null) {
            reviews = new ArrayList<>();
        }
        return reviews;
    }

    public List<PriceEntity> getPrices() {
        if (prices == null) {
            prices = new ArrayList<>();
        }
        return prices;
    }

    public List<TaxonomyEntity> getTaxonomies() {
        if (taxonomies == null) {
            taxonomies = new ArrayList<>();
        }
        return taxonomies;
    }
}