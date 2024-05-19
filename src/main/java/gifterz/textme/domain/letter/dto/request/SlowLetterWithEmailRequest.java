package gifterz.textme.domain.letter.dto.request;

import jakarta.validation.constraints.Email;

public record SlowLetterWithEmailRequest(
        @Email
        String email,
        String senderName,
        String contents,
        String imageUrl
) {
    public SenderInfo toSenderInfo() {
        return SenderInfo.ofEmailSenderNameImage(email, senderName);
    }

    public Target toTarget() {
        return Target.of(contents, imageUrl);
    }
}
