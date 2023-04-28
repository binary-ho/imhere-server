package gdsc.binaryho.imhere.exception.member;

import gdsc.binaryho.imhere.exception.ErrorCode;
import gdsc.binaryho.imhere.exception.ImhereException;

public class EmailDuplicatedException extends ImhereException {

    public static final ImhereException EXCEPTION = new EmailDuplicatedException();

    private EmailDuplicatedException() {
        super(ErrorCode.EMAIL_DUPLICATED);
    }
}
