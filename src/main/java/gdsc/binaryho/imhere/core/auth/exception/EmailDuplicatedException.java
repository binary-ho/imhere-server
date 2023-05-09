package gdsc.binaryho.imhere.core.auth.exception;

import gdsc.binaryho.imhere.exception.ErrorInfo;
import gdsc.binaryho.imhere.exception.ImhereException;

public class EmailDuplicatedException extends ImhereException {

    public static final ImhereException EXCEPTION = new EmailDuplicatedException();

    private EmailDuplicatedException() {
        super(ErrorInfo.EMAIL_DUPLICATED);
    }
}
