package gdsc.binaryho.imhere.core.auth.exception;

import gdsc.binaryho.imhere.exception.ErrorInfo;
import gdsc.binaryho.imhere.exception.ImhereException;

public class PermissionDeniedException extends ImhereException {

    public static final ImhereException EXCEPTION = new PermissionDeniedException();

    private PermissionDeniedException() {
        super(ErrorInfo.PERMISSION_DENIED);
    }
}
