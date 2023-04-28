package gdsc.binaryho.imhere.exception.attendance;

import gdsc.binaryho.imhere.exception.ErrorCode;
import gdsc.binaryho.imhere.exception.ImhereException;

public class AttendanceTimeExceededException extends ImhereException {

    public static final ImhereException EXCEPTION = new AttendanceTimeExceededException();

    private AttendanceTimeExceededException() {
        super(ErrorCode.ATTENDANCE_TIME_EXCEEDED);
    }
}
