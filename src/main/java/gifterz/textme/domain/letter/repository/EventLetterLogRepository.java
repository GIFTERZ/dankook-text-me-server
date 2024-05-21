package gifterz.textme.domain.letter.repository;

import gifterz.textme.domain.letter.entity.EventLetterLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(readOnly = true)
public interface EventLetterLogRepository extends JpaRepository<EventLetterLog, Long> {

    List<EventLetterLog> findAllByUserId(Long userId);
}
