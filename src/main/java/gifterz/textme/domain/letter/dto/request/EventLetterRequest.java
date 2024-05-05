package gifterz.textme.domain.letter.dto.request;

public record EventLetterRequest(
        String contents,
        String senderName,
        String imageUrl,
        String contactInfo
) {
    public SenderInfo toSenderInfo() {
        return SenderInfo.ofSenderNameContactInfo(senderName, contactInfo);
    }

    public Target toTarget() {
        return Target.of(contents, imageUrl);
    }
}
