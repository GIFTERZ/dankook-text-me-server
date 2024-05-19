package gifterz.textme.domain.letter.repository;

import gifterz.textme.domain.letter.entity.EventLetterLog;
import gifterz.textme.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(readOnly = true)
public interface EventLetterLogRepository extends JpaRepository<EventLetterLog, Long> {

    @Query("select count(e) from EventLetterLog e where e.user = ?1")
    long countByUser(User user);

    List<EventLetterLog> findAllByUserId(Long userId);
}
