package gdsc.binaryho.imhere.exception.enrollment;

import gdsc.binaryho.imhere.exception.ErrorCode;
import gdsc.binaryho.imhere.exception.ImhereException;

public class EnrollmentNotApprovedException extends ImhereException {

    public static final ImhereException EXCEPTION = new EnrollmentNotApprovedException();

    private EnrollmentNotApprovedException() {
        super(ErrorCode.ENROLLMENT_NOT_APPROVED);
    }
}
