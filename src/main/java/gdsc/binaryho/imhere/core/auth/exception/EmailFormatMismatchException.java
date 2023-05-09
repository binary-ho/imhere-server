package gdsc.binaryho.imhere.core.auth.exception;

import gdsc.binaryho.imhere.exception.ErrorInfo;
import gdsc.binaryho.imhere.exception.ImhereException;

public class EmailFormatMismatchException extends ImhereException {

    public static final ImhereException EXCEPTION = new EmailFormatMismatchException();

    private EmailFormatMismatchException() {
        super(ErrorInfo.EMAIL_FORMAT_MISMATCH);
    }
}
