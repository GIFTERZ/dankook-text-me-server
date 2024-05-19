package gifterz.textme.domain.letter.dto.response;

import gifterz.textme.domain.letter.dto.request.ReceiverInfo;
import gifterz.textme.domain.letter.dto.request.SenderInfo;

public record SlowLetterWithAddressResponse(
        SenderInfo senderInfo,
        ReceiverInfo receiverInfo,
        String contents
) {
}
