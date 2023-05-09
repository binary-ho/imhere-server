package gdsc.binaryho.imhere.core.enrollment.exception;

import gdsc.binaryho.imhere.exception.ErrorInfo;
import gdsc.binaryho.imhere.exception.ImhereException;

public class EnrollmentNotApprovedException extends ImhereException {

    public static final ImhereException EXCEPTION = new EnrollmentNotApprovedException();

    private EnrollmentNotApprovedException() {
        super(ErrorInfo.ENROLLMENT_NOT_APPROVED);
    }
}
