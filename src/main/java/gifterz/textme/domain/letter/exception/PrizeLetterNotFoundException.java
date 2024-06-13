package gifterz.textme.domain.letter.exception;

import gifterz.textme.error.ErrorCode;
import gifterz.textme.error.exception.ApplicationException;

public class PrizeLetterNotFoundException extends ApplicationException {
    public PrizeLetterNotFoundException() {
        super(ErrorCode.RESOURCE_NOT_FOUND);
    }
}
