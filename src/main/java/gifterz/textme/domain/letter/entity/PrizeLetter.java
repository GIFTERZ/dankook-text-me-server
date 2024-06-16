package gifterz.textme.domain.letter.entity;

import gifterz.textme.domain.entity.BaseEntity;
import gifterz.textme.domain.entity.StatusType;
import gifterz.textme.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PrizeLetter extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @Column(length = 500, nullable = false)
    private String contents;

    @Column(length = 125)
    private String webInfoImageUrl;

    @Column(length = 125)
    private String paymentImageUrl;

    private String cardImageUrl;

    private Category category;

    private String phoneNumber;

    private PrizeLetter(User user, String contents, String webInfoImageUrl, String paymentImageUrl,
                        String cardImageUrl, Category category, String phoneNumber) {
        super(StatusType.ACTIVATE.getStatus());
        this.user = user;
        this.contents = contents;
        this.webInfoImageUrl = webInfoImageUrl;
        this.paymentImageUrl = paymentImageUrl;
        this.cardImageUrl = cardImageUrl;
        this.category = category;
        this.phoneNumber = phoneNumber;
    }

    public static PrizeLetter of(User user, String contents, String webInfoImageUrl, String paymentImageUrl,
                                 String cardImageUrl, Category category, String phoneNumber) {
        return new PrizeLetter(user, contents, webInfoImageUrl, paymentImageUrl, cardImageUrl, category, phoneNumber);
    }

}
