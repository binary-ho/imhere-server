package gdsc.binaryho.imhere.core.auth.exception;

import gdsc.binaryho.imhere.exception.ErrorCode;
import gdsc.binaryho.imhere.exception.ImhereException;

public class EmailVerificationCodeIncorrectException extends ImhereException {

    public static final ImhereException EXCEPTION = new EmailVerificationCodeIncorrectException();

    private EmailVerificationCodeIncorrectException() {
        super(ErrorCode.EMAIL_VERIFICATION_CODE_INCORRECT);
    }
}
