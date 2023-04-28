package gdsc.binaryho.imhere.exception.member;

import gdsc.binaryho.imhere.exception.ErrorCode;
import gdsc.binaryho.imhere.exception.ImhereException;

public class EmailFormatMismatchException extends ImhereException {

    public static final ImhereException EXCEPTION = new EmailFormatMismatchException();

    private EmailFormatMismatchException() {
        super(ErrorCode.EMAIL_FORMAT_MISMATCH);
    }
}
