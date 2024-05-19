package gifterz.textme.domain.notification.entity;

import gifterz.textme.domain.entity.BaseEntity;
import gifterz.textme.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import static gifterz.textme.domain.entity.StatusType.ACTIVATE;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User receiver;

    @Column
    private String content;

    @Column
    private boolean isRead;

    @Column
    private boolean isDeleted;

    private Notification(User receiver, String content, boolean isRead, boolean isDeleted) {
        super(ACTIVATE.getStatus());
        this.receiver = receiver;
        this.content = content;
        this.isRead = isRead;
        this.isDeleted = isDeleted;
    }


    public static Notification of(User receiver, String content) {
        return new Notification(receiver, content, false, false);
    }
}
