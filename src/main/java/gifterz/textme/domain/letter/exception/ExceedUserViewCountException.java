package gifterz.textme.domain.letter.exception;

import gifterz.textme.error.ErrorCode;
import gifterz.textme.error.exception.ApplicationException;

import static gifterz.textme.domain.letter.entity.EventLetter.MAX_VIEW_COUNT;

public class ExceedUserViewCountException extends ApplicationException {
    public ExceedUserViewCountException() {
        super("이미 " + MAX_VIEW_COUNT + "번 조회한 사용자입니다.", ErrorCode.ACCESS_DENIED);
    }

}
