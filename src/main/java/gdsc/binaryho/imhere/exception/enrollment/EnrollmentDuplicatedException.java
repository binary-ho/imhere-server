package gdsc.binaryho.imhere.exception.enrollment;

import gdsc.binaryho.imhere.exception.ErrorCode;
import gdsc.binaryho.imhere.exception.ImhereException;

public class EnrollmentDuplicatedException extends ImhereException {

    public static final ImhereException EXCEPTION = new EnrollmentDuplicatedException();

    private EnrollmentDuplicatedException() {
        super(ErrorCode.ENROLLMENT_DUPLICATED);
    }
}
