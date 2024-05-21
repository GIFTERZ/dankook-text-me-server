package gifterz.textme.domain.letter.dto.response;


import gifterz.textme.domain.letter.entity.EventLetter;

public record EventLetterResponse(
        Long id,
        String senderName,
        String contents,
        String imageUrl,
        String contactInfo

) {
    public static EventLetterResponse from(EventLetter eventLetter) {
        return new EventLetterResponse(
                eventLetter.getId(),
                eventLetter.getSenderName(),
                eventLetter.getContents(),
                eventLetter.getImageUrl(),
                eventLetter.getContactInfo());
    }
}
