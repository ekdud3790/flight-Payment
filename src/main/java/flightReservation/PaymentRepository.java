package flightReservation;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PaymentRepository extends CrudRepository<Payment, Long>{

    Optional<Payment> findByflightId(String flightId);

}