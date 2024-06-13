package gifterz.textme.s3Proxy.exception;

import gifterz.textme.error.ErrorCode;
import gifterz.textme.error.exception.InvalidValueException;

public class InvalidFileException extends InvalidValueException {
    public InvalidFileException() {
        super(ErrorCode.Illegal_FILE);
    }
}
