package gifterz.textme.domain.letter.dto.request;

import gifterz.textme.domain.letter.entity.Category;
import gifterz.textme.domain.letter.entity.PrizeLetterVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public record PrizeLetterRequest(
        String contents,
        MultipartFile webInfoImage,
        Optional<MultipartFile> paymentImage,
        String cardImageUrl,
        String category
) {
    public PrizeLetterVO toPrizeLetterVO() {
        Category category = Category.fromName(this.category);
        return paymentImage.map(
                        paymentImage ->
                                PrizeLetterVO.of(contents, webInfoImage, paymentImage, cardImageUrl, category))
                .orElseGet(() ->
                        PrizeLetterVO.of(contents, webInfoImage, null, cardImageUrl, category));
    }
}
