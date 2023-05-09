package gdsc.binaryho.imhere.core.auth.exception;

import gdsc.binaryho.imhere.exception.ErrorInfo;
import gdsc.binaryho.imhere.exception.ImhereException;

public class PasswordFormatMismatchException extends ImhereException {

    public static final ImhereException EXCEPTION = new PasswordFormatMismatchException();

    private PasswordFormatMismatchException() {
        super(ErrorInfo.PASSWORD_FORMAT_MISMATCH);
    }
}
