package gdsc.binaryho.imhere.core.attendance.domain;

import static gdsc.binaryho.imhere.core.attendance.application.AttendanceSaveRequestStatus.SUCCESS;
import static gdsc.binaryho.imhere.core.attendance.application.AttendanceSaveRequestStatus.PROCESSING;
import static gdsc.binaryho.imhere.core.attendance.application.AttendanceSaveRequestStatus.FAILED;

import gdsc.binaryho.imhere.config.redis.RedisKeyConstants;
import gdsc.binaryho.imhere.core.attendance.application.AttendanceSaveRequestStatus;
import gdsc.binaryho.imhere.domain.CacheEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AttendanceHistory extends CacheEntity {

    private static final String KEY_FORMAT = RedisKeyConstants.ATTENDANCE_HISTORY_KEY_FORMAT;

    private final long lectureId;
    private final long studentId;
    private final AttendanceSaveRequestStatus attendanceSaveRequestStatus;

    public static AttendanceHistory createAwaitAttendanceHistory(long lectureId, long studentId) {
        return new AttendanceHistory(lectureId, studentId, PROCESSING);
    }

    public static AttendanceHistory createAcceptedAttendanceHistory(long lectureId, long studentId) {
        return new AttendanceHistory(lectureId, studentId, SUCCESS);
    }

    public static AttendanceHistory createFailedAttendanceHistory(long lectureId, long studentId) {
        return new AttendanceHistory(lectureId, studentId, FAILED);
    }

    @Override
    public String getKey() {
        return String.format(KEY_FORMAT, lectureId, studentId);
    }

    public static String convertToKey(long lectureId, long studentId) {
        return String.format(KEY_FORMAT, lectureId, studentId);
    }
}
