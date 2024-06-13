package gifterz.textme.s3Proxy.exception;

import gifterz.textme.error.ErrorCode;
import gifterz.textme.error.exception.InvalidValueException;

public class ResizeFileException extends InvalidValueException {
    public ResizeFileException() {
        super(ErrorCode.FAIL_FILE_RESIZE);
    }
}
