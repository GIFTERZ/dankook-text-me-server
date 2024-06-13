package gifterz.textme.domain.letter.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllPrizeLettersResponse {
    private Long id;
    private String senderName;
    private String cardImageUrl;
}
