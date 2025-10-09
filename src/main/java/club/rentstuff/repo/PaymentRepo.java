package club.rentstuff.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import club.rentstuff.entity.PaymentEntity;

public interface PaymentRepo extends JpaRepository<PaymentEntity, Long> {

}
