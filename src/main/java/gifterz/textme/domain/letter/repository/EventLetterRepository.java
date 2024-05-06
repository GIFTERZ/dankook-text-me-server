package gifterz.textme.domain.letter.repository;

import gifterz.textme.domain.letter.entity.EventLetter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface EventLetterRepository extends JpaRepository<EventLetter, Long> {
}