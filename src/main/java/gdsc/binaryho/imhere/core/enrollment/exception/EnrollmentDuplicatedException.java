package gdsc.binaryho.imhere.core.enrollment.exception;

import gdsc.binaryho.imhere.exception.ErrorInfo;
import gdsc.binaryho.imhere.exception.ImhereException;

public class EnrollmentDuplicatedException extends ImhereException {

    public static final ImhereException EXCEPTION = new EnrollmentDuplicatedException();

    private EnrollmentDuplicatedException() {
        super(ErrorInfo.ENROLLMENT_DUPLICATED);
    }
}
