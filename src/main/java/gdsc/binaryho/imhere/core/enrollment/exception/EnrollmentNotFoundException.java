package gdsc.binaryho.imhere.core.enrollment.exception;

import gdsc.binaryho.imhere.exception.ErrorInfo;
import gdsc.binaryho.imhere.exception.ImhereException;

public class EnrollmentNotFoundException extends ImhereException {

    public static final ImhereException EXCEPTION = new EnrollmentNotFoundException();

    private EnrollmentNotFoundException() {
        super(ErrorInfo.ENROLLMENT_NOT_FOUND);
    }
}
