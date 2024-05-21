package gifterz.textme.domain.letter.exception;

import gifterz.textme.error.ErrorCode;
import gifterz.textme.error.exception.ApplicationException;

public class AlreadyPostedUserException extends ApplicationException {
    public AlreadyPostedUserException() {
        super("사용자당 하나의 편지만 전송할 수 있습니다.", ErrorCode.ACCESS_DENIED);
    }
}
