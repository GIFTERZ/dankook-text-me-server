package gifterz.textme.domain.user.repository;

import gifterz.textme.domain.user.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MajorRepository extends JpaRepository<Major, Long> {

    Optional<Major> findByDepartmentAndName(String department, String name);
}
