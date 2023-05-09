package gdsc.binaryho.imhere.core.auth.exception;

import gdsc.binaryho.imhere.exception.ErrorInfo;
import gdsc.binaryho.imhere.exception.ImhereException;

public class MemberNotFoundException extends ImhereException {

    public static final ImhereException EXCEPTION = new MemberNotFoundException();

    private MemberNotFoundException() {
        super(ErrorInfo.MEMBER_NOT_FOUND);
    }
}
