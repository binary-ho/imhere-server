package gdsc.binaryho.imhere.exception.member;

import gdsc.binaryho.imhere.exception.ErrorCode;
import gdsc.binaryho.imhere.exception.ImhereException;

public class PasswordIncorrectException extends ImhereException {

    public static final ImhereException EXCEPTION = new PasswordIncorrectException();

    private PasswordIncorrectException() {
        super(ErrorCode.PASSWORD_INCORRECT);
    }
}
