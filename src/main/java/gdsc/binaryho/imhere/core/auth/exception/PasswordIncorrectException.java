package gdsc.binaryho.imhere.core.auth.exception;

import gdsc.binaryho.imhere.exception.ErrorInfo;
import gdsc.binaryho.imhere.exception.ImhereException;

public class PasswordIncorrectException extends ImhereException {

    public static final ImhereException EXCEPTION = new PasswordIncorrectException();

    private PasswordIncorrectException() {
        super(ErrorInfo.PASSWORD_INCORRECT);
    }
}
