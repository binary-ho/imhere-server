package gdsc.binaryho.imhere.core.auth.exception;

import gdsc.binaryho.imhere.exception.ErrorInfo;
import gdsc.binaryho.imhere.exception.ImhereException;

public class PasswordNullException extends ImhereException {

    public static final ImhereException EXCEPTION = new PasswordNullException();

    private PasswordNullException() {
        super(ErrorInfo.PASSWORD_NULL_EXCEPTION);
    }
}
