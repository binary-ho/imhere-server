package gdsc.binaryho.imhere.core.attendance.exception;

import gdsc.binaryho.imhere.exception.ErrorInfo;
import gdsc.binaryho.imhere.exception.ImhereException;

public class AttendanceTimeExceededException extends ImhereException {

    public static final ImhereException EXCEPTION = new AttendanceTimeExceededException();

    private AttendanceTimeExceededException() {
        super(ErrorInfo.ATTENDANCE_TIME_EXCEEDED);
    }
}
