package gifterz.textme.s3Proxy.exception;

import gifterz.textme.error.ErrorCode;
import gifterz.textme.error.exception.ApplicationException;

public class FileTooLargeException extends ApplicationException {
    public FileTooLargeException() {
        super(ErrorCode.FILE_TOO_LARGE);
    }
}
