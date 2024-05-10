package gifterz.textme.domain.letter.repository;

import gifterz.textme.domain.letter.entity.EventLetter;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface EventLetterRepository extends JpaRepository<EventLetter, Long> {

    @Lock(value = LockModeType.OPTIMISTIC)
    @Query("select e from EventLetter e where e.id = :id and e.status = :status")
    Optional<EventLetter> findByIdWithOptimistic(Long id, String status);
}
