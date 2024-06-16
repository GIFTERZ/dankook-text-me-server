package gifterz.textme.domain.letter.entity;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class PrizeLetterVO {
    private String contents;
    private MultipartFile webInfoImage;
    private MultipartFile paymentImage;
    private String cardImageUrl;
    private Category category;

    private PrizeLetterVO(String contents, MultipartFile webInfoImage, MultipartFile paymentImage,
                          String cardImageUrl, Category category) {
        this.contents = contents;
        this.webInfoImage = webInfoImage;
        this.paymentImage = paymentImage;
        this.cardImageUrl = cardImageUrl;
        this.category = category;
    }

    public static PrizeLetterVO of(String contents, MultipartFile webInfoImage, MultipartFile paymentImage,
                                   String cardImageUrl, Category category) {
        return new PrizeLetterVO(contents, webInfoImage, paymentImage, cardImageUrl, category);
    }
}
