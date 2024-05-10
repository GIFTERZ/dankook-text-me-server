package gifterz.textme.domain.letter.exception;

import gifterz.textme.error.ErrorCode;
import gifterz.textme.error.exception.ApplicationException;

public class AlreadyViewedUserException extends ApplicationException {
    public AlreadyViewedUserException() {
        super("이미 조회한 사용자입니다.", ErrorCode.ACCESS_DENIED);
    }
}
