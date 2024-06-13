package gifterz.textme.domain.letter.dto.request;

import gifterz.textme.domain.letter.entity.Category;
import gifterz.textme.domain.letter.entity.PrizeLetterVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record PrizeLetterRequest(
        String contents,
        List<MultipartFile> images,
        String cardImageUrl,
        String category
) {
    public PrizeLetterVO toPrizeLetterVO() {
        if (images.size() != 2) {
            throw new IllegalArgumentException("이미지는 2개가 필요합니다.");
        }
        return PrizeLetterVO.of(contents, images.get(0), images.get(1), cardImageUrl, Category.fromName(category));
    }
}
