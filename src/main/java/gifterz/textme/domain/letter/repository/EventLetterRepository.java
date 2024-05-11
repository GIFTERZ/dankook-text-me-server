package gifterz.textme.domain.letter.repository;

import gifterz.textme.domain.letter.entity.EventLetter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface EventLetterRepository extends JpaRepository<EventLetter, Long> {
    List<EventLetter> findAllByUserGenderAndStatus(String gender, String status);
}