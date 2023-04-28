package gdsc.binaryho.imhere.core.attendance.exception;

import gdsc.binaryho.imhere.exception.ErrorCode;
import gdsc.binaryho.imhere.exception.ImhereException;

public class AttendanceNumberIncorrectException extends ImhereException {

    public static final ImhereException EXCEPTION = new AttendanceNumberIncorrectException();

    private AttendanceNumberIncorrectException() {
        super(ErrorCode.ATTENDANCE_NUMBER_INCORRECT);
    }
}
