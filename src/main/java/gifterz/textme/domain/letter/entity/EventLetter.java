package gifterz.textme.domain.letter.entity;

import gifterz.textme.domain.entity.BaseEntity;
import gifterz.textme.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public static EventLetter of(User user, String senderName, String contents,
                                 String imageUrl, String contactInfo) {
        return new EventLetter(user, senderName, contents, imageUrl, contactInfo, 0);
    }

    private EventLetter(User user, String senderName, String contents,
                        String imageUrl, String contactInfo, Integer viewCount) {
        this.user = user;
        this.senderName = senderName;
        this.contents = contents;
        this.imageUrl = imageUrl;
        this.contactInfo = contactInfo;
        this.viewCount = viewCount;
    }
}
