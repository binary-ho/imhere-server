package gdsc.binaryho.imhere.core.auth.exception;

import gdsc.binaryho.imhere.exception.ErrorInfo;
import gdsc.binaryho.imhere.exception.ImhereException;

public class RequestMemberIdMismatchException extends ImhereException {

    public static final ImhereException EXCEPTION = new RequestMemberIdMismatchException();

    private RequestMemberIdMismatchException() {
        super(ErrorInfo.REQUEST_MEMBER_ID_MISMATCH);
    }
}
