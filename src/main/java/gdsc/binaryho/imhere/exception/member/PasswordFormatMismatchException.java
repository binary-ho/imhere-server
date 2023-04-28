package gdsc.binaryho.imhere.exception.member;

import gdsc.binaryho.imhere.exception.ErrorCode;
import gdsc.binaryho.imhere.exception.ImhereException;

public class PasswordFormatMismatchException extends ImhereException {

    public static final ImhereException EXCEPTION = new PasswordFormatMismatchException();

    private PasswordFormatMismatchException() {
        super(ErrorCode.PASSWORD_FORMAT_MISMATCH);
    }
}
