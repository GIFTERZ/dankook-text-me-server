package gifterz.textme.domain.letter.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SenderInfo {
    private final String senderName;
    private String phoneNumber;
    private String email;
    private String contactInfo;

    public static SenderInfo fromSenderName(String senderName) {
        return SenderInfo.builder()
                .senderName(senderName)
                .build();
    }


    public static SenderInfo ofSenderNameContactInfo(String senderName, String contactInfo) {
        return SenderInfo.builder()
                .senderName(senderName)
                .contactInfo(contactInfo)
                .build();
    }

    public static SenderInfo ofEmailSenderNameImage(String email, String senderName) {
        return SenderInfo.builder()
                .email(email)
                .senderName(senderName)
                .build();
    }
}
