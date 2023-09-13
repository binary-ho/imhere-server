package gdsc.binaryho.imhere.core.auth.exception;

import gdsc.binaryho.imhere.exception.ErrorInfo;
import gdsc.binaryho.imhere.exception.ImhereException;

public class PasswordChangeMemberNotExistException extends ImhereException {

    public static final ImhereException EXCEPTION = new PasswordChangeMemberNotExistException();

    private PasswordChangeMemberNotExistException() {
        super(ErrorInfo.PASSWORD_CHANGE_MEMBER_NOT_EXIST);
    }
}
