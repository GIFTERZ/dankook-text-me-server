package gifterz.textme.s3Proxy.exception;

import gifterz.textme.error.ErrorCode;
import gifterz.textme.error.exception.ApplicationException;

public class UploadFileException extends ApplicationException {
    public UploadFileException() {
        super(ErrorCode.FILE_UPLOAD_ERROR);
    }
}
