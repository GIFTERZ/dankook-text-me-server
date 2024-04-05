package gifterz.textme.domain.user.entity;

import gifterz.textme.domain.entity.BaseEntity;
import gifterz.textme.domain.entity.StatusType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Major extends BaseEntity {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String department;
    @Column(nullable = false)
    private String name;

    private Major(String department, String name) {
        super(StatusType.ACTIVATE.getStatus());
        this.department = department;
        this.name = name;
    }

    public static Major of(String department, String name) {
        return new Major(department, name);
    }
}
