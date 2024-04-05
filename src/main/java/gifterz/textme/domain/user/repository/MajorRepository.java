package gifterz.textme.domain.user.repository;

import gifterz.textme.domain.user.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MajorRepository extends JpaRepository<Major, Long> {

    boolean existsByDepartmentAndName(String department, String name);
}
