package club.rentstuff.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import club.rentstuff.entity.BookingEntity;


public interface BookingRepo extends JpaRepository<BookingEntity, Long> {

    @Query("SELECT b FROM BookingEntity b WHERE b.item.id = :itemId " +
            "AND b.startDate <= :endDate AND b.endDate >= :startDate")
     List<BookingEntity> findOverlappingBookings(
         @Param("itemId") Long itemId,
         @Param("startDate") LocalDateTime startDate,
         @Param("endDate") LocalDateTime endDate
     );
    

	List<BookingEntity> findByItemId(Long itemId);


}
