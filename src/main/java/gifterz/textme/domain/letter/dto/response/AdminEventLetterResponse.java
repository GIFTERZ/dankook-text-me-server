package gifterz.textme.domain.letter.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AdminEventLetterResponse {
    private Long id;
    private String senderName;
    private String contents;
    private String imageUrl;
    private String contactInfo;
    private int viewCount;
    private String status;
}
