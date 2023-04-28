package gdsc.binaryho.imhere.exception.member;

import gdsc.binaryho.imhere.exception.ErrorCode;
import gdsc.binaryho.imhere.exception.ImhereException;

public class PermissionDeniedException extends ImhereException {

    public static final ImhereException EXCEPTION = new PermissionDeniedException();

    private PermissionDeniedException() {
        super(ErrorCode.PERMISSION_DENIED);
    }
}
