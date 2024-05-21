package gifterz.textme.domain.letter.dto.response;

import gifterz.textme.domain.letter.entity.EventLetter;

public record WhoseEventLetterResponse(
        Long id,
        String senderName,
        String contents,
        String imageUrl,
        String contactInfo,
        boolean isMine

) {
    public static WhoseEventLetterResponse of(EventLetter eventLetter, boolean isMine) {
        return new WhoseEventLetterResponse(
                eventLetter.getId(),
                eventLetter.getSenderName(),
                eventLetter.getContents(),
                eventLetter.getImageUrl(),
                eventLetter.getContactInfo(),
                isMine
        );
    }
}
