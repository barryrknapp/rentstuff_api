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
import jakarta.persistence.OneToMany;
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
@Table(name = "USER_DETAIL")
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class UserEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    @SequenceGenerator(name = "user_generator", sequenceName = "user_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "EMAIL", unique = true, nullable = false)
    private String email;

    @Column(name = "PASSWORD", nullable = false, length = 60) // BCrypt hash length
    private String password;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "CREATE_DATE")
    private LocalDateTime createDate;

    @Column(name = "MODIFY_DATE")
    private LocalDateTime modifyDate;

	@ToString.Exclude
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RentalItemEntity> rentalItems;

	@ToString.Exclude
    @OneToMany(mappedBy = "renter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookingEntity> bookings;
    
	@ToString.Exclude
    @OneToMany(mappedBy = "reviewedUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReviewUserEntity> reviews;

    @Column(name = "ROLE")
    private String role; // e.g., USER, ADMIN
    
    
    public List<RentalItemEntity> getRentalItems() {
        return rentalItems!=null? rentalItems: new ArrayList<>();
    }
    
    public List<BookingEntity> getBookings() {
        return bookings!=null? bookings: new ArrayList<>();
    }
    
    public List<ReviewUserEntity> getReviews() {
        return reviews!=null? reviews: new ArrayList<>();
    }
    
}