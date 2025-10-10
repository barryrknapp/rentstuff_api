package club.rentstuff.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Entity
@Table(name = "RENTAL_ITEM_IMAGE")
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class RentalItemImageEntity {


    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rental_item_image_generator")
    @SequenceGenerator(name = "rental_item_image_generator", sequenceName = "rental_item_image_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "RENTAL_ITEM_ID", nullable = false)
    private RentalItemEntity rentalItem;

    
    
    @Column(name = "IMAGE_URL", nullable = false)
    private String imageUrl;
    
    @Column(name = "CREATE_DATE")
    private LocalDateTime createDate;

    @Column(name = "MODIFY_DATE")
    private LocalDateTime modifyDate;
}