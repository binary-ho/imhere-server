package gdsc.binaryho.imhere.core.auth.exception;

import gdsc.binaryho.imhere.exception.ErrorInfo;
import gdsc.binaryho.imhere.exception.ImhereException;

public class PasswordsNotEqualException extends ImhereException {

    public static final ImhereException EXCEPTION = new PasswordsNotEqualException();

    private PasswordsNotEqualException() {
        super(ErrorInfo.PASSWORDS_NOT_EQUAL);
    }
}
