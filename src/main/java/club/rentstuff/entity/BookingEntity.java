package club.rentstuff.entity;

import java.math.BigDecimal;
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
import jakarta.persistence.ManyToOne;
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
@Table(name = "BOOKING")
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class BookingEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "booking_generator")
    @SequenceGenerator(name = "booking_generator", sequenceName = "booking_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ITEM_ID", nullable = false)
    private RentalItemEntity item;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserEntity renter;

    @Column(name = "START_DATE", nullable = false)
    private LocalDateTime  startDate;

    @Column(name = "END_DATE", nullable = false)
    private LocalDateTime  endDate;

    @Column(name = "STATUS")
    private String status; // e.g., PENDING, CONFIRMED, CANCELLED

    @Column(name = "CREATE_DATE")
    private LocalDateTime createDate;

    @Column(name = "MODIFY_DATE")
    private LocalDateTime modifyDate;
    
    @Column(name = "TOTAL_PRICE")
	private BigDecimal totalPrice;
    
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaymentEntity> payments = new ArrayList<>();
    


    public List<PaymentEntity> getPayments() {
        return payments!=null? payments: new ArrayList<>();
    }
    
    
    
}