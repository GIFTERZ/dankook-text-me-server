package gifterz.textme.domain.letter.exception;

import gifterz.textme.error.ErrorCode;
import gifterz.textme.error.exception.ApplicationException;

public class NotPostedUserException extends ApplicationException {
    public NotPostedUserException() {
        super("편지를 작성하지 않은 사용자는 편지를 볼 수 없습니다.", ErrorCode.ACCESS_DENIED);
    }
}
