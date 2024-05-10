package gifterz.textme.domain.letter.entity;

import gifterz.textme.domain.entity.BaseEntity;
import gifterz.textme.domain.entity.StatusType;
import gifterz.textme.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventLetter extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @Column(length = 25, nullable = false)
    private String senderName;

    @Column(length = 500, nullable = false)
    private String contents;

    @Column(length = 125)
    private String imageUrl;

    @Column(length = 200)
    private String contactInfo;

    private Integer viewCount;

    @Version
    private Long version;

    public final static int MAX_VIEW_COUNT = 3;

    public static EventLetter of(User user, String senderName, String contents,
                                 String imageUrl, String contactInfo) {
        return new EventLetter(user, senderName, contents, imageUrl, contactInfo, 0);
    }

    private EventLetter(User user, String senderName, String contents,
                        String imageUrl, String contactInfo, Integer viewCount) {
        super(StatusType.ACTIVATE.getStatus());
        this.user = user;
        this.senderName = senderName;
        this.contents = contents;
        this.imageUrl = imageUrl;
        this.contactInfo = contactInfo;
        this.viewCount = viewCount;
    }

    public void increaseViewCount() {
        this.viewCount++;
        if (this.viewCount >= MAX_VIEW_COUNT) {
            this.deactivate();
        }
    }

    public void deactivate() {
        this.status = StatusType.DEACTIVATE.getStatus();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventLetter that = (EventLetter) o;

        return Objects.equals(id, that.id) &&
                Objects.equals(user, that.user) &&
                Objects.equals(senderName, that.senderName) &&
                Objects.equals(contents, that.contents) &&
                Objects.equals(imageUrl, that.imageUrl) &&
                Objects.equals(contactInfo, that.contactInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, senderName, contents, imageUrl, contactInfo);
    }
}
