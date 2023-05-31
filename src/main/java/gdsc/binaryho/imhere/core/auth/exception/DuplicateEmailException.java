package gdsc.binaryho.imhere.core.auth.exception;

import gdsc.binaryho.imhere.exception.ErrorInfo;
import gdsc.binaryho.imhere.exception.ImhereException;

public class DuplicateEmailException extends ImhereException {

    public static final ImhereException EXCEPTION = new DuplicateEmailException();

    private DuplicateEmailException() {
        super(ErrorInfo.EMAIL_DUPLICATED);
    }
}
