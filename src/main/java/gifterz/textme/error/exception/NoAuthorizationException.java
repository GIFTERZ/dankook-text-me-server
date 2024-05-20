package gifterz.textme.error.exception;

import gifterz.textme.error.ErrorCode;

public class NoAuthorizationException extends ApplicationException {

    public NoAuthorizationException(String message) {
        super(message, ErrorCode.ACCESS_DENIED);
    }
}
