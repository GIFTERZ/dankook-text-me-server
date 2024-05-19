package gifterz.textme.domain.user.entity;

import gifterz.textme.domain.entity.BaseEntity;
import gifterz.textme.domain.entity.StatusType;
import gifterz.textme.domain.oauth.entity.AuthType;
import gifterz.textme.global.auth.role.UserRole;
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

    @Column(length = 30, nullable = false)
    private String name;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private AuthType authType;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Major major;

    @Column(length = 10)
    private String gender;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private UserRole userRole;

    public static User of(String email, String name, AuthType authType) {
        return new User(email, name, authType);
    }

    public static User of(String email, String name, AuthType authType, Major major, String gender) {
        return new User(email, name, authType, major, gender);
    }

    private User(String email, String name, AuthType authType) {
        super(StatusType.ACTIVATE.getStatus());
        this.email = email;
        this.name = name;
        this.authType = authType;
        this.userRole = UserRole.USER;
    }

    private User(String email, String name, AuthType authType, Major major, String gender) {
        super(StatusType.ACTIVATE.getStatus());
        this.email = email;
        this.name = name;
        this.authType = authType;
        this.major = major;
        this.gender = gender;
        this.userRole = UserRole.USER;
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
    public void updateGender(String gender) {
        this.gender = gender;
    }


    public boolean isUnAuthorized(User user) {
        return this != user;
    }
}
