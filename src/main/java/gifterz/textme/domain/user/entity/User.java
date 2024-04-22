package gifterz.textme.domain.user.entity;

import gifterz.textme.domain.entity.BaseEntity;
import gifterz.textme.domain.entity.StatusType;
import gifterz.textme.domain.oauth.entity.AuthType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private AuthType authType;

    @JoinColumn(name = "major_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Major major;

    public static User of(String email, String name, AuthType authType) {
        return new User(email, name, authType);
    }

    public static User of(String email, String name, AuthType authType, Major major) {
        return new User(email, name, authType, major);
    }

    private User(String email, String name, AuthType authType) {
        super(StatusType.ACTIVATE.getStatus());
        this.email = email;
        this.name = name;
        this.authType = authType;
    }

    private User(String email, String name, AuthType authType, Major major) {
        super(StatusType.ACTIVATE.getStatus());
        this.email = email;
        this.name = name;
        this.authType = authType;
        this.major = major;
    }

    public void updateUserName(String name) {
        this.name = name;
    }

    public void updateAuthType(AuthType authType) {
        this.authType = authType;
    }

    public void updateMajor(Major major) {
        this.major = major;
    }

    public boolean isUnAuthorized(User user) {
        return this != user;
    }
}
