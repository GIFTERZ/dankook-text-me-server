package gifterz.textme.domain.letter.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrizeLetterResponse {
    private Long id;
    private String senderName;
    private String contents;
    private String webInfoImageUrl;
    private String paymentImageUrl;
    private String cardImageUrl;
    private String category;
}
