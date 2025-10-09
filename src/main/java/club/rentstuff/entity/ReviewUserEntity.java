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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "REVIEW_USER")
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class ReviewUserEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_user_generator")
    @SequenceGenerator(name = "review_user_generator", sequenceName = "review_user_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "REVIEWED_USER_ID", nullable = false)
    private UserEntity reviewedUser;

    @ManyToOne
    @JoinColumn(name = "REVIEWER_ID", nullable = false)
    private UserEntity reviewer;

    @Column(name = "RATING", nullable = false)
    private Integer rating; // 1-5 stars

    @Column(name = "COMMENT", length = 1000)
    private String comment;

    @Column(name = "CREATE_DATE")
    private LocalDateTime createDate;

    @Column(name = "MODIFY_DATE")
    private LocalDateTime modifyDate;
}