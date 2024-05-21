package gifterz.textme.domain.letter.repository;

import gifterz.textme.domain.letter.entity.EventLetter;
import gifterz.textme.domain.user.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface EventLetterRepository extends JpaRepository<EventLetter, Long> {

    List<EventLetter> findAllByUserGenderAndStatus(String gender, String status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from EventLetter e where e.id = :id and e.status = :status")
    Optional<EventLetter> findByIdWithPessimistic(Long id, String status);

    List<EventLetter> findAllByStatus(String status);

    @Query("select e from EventLetter e where e.contactInfo is not null and e.status = :status")
    List<EventLetter> findByContactInfoContaining(String status);

    @Query("select e FROM EventLetter e " +
            "WHERE e.contactInfo is not null and e.user.gender = :gender and e.status = :status")
    List<EventLetter> findAllByContactInfoContainingAndGender(String gender, String status);

    Optional<EventLetter> findByUser(User user);
}
