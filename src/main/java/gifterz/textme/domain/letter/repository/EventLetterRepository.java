package gifterz.textme.domain.letter.repository;

import gifterz.textme.domain.letter.entity.EventLetter;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface EventLetterRepository extends JpaRepository<EventLetter, Long> {

    @Lock(value = LockModeType.OPTIMISTIC)
    Optional<EventLetter> findByIdWithOptimistic(Long id, String status);
}