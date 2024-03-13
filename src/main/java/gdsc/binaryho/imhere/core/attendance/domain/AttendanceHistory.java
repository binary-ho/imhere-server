package gdsc.binaryho.imhere.core.attendance.domain;

import gdsc.binaryho.imhere.config.redis.RedisKeyConstants;
import gdsc.binaryho.imhere.domain.CacheEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AttendanceHistory extends CacheEntity {

    private static final String KEY_FORMAT = RedisKeyConstants.ATTENDANCE_HISTORY_KEY_FORMAT;

    private final long lectureId;
    private final long studentId;
    private final String timestamp;

    public static AttendanceHistory of(long lectureId, long studentId, String timestamp) {
        return new AttendanceHistory(lectureId, studentId, timestamp);
    }

    @Override
    public String getKey() {
        return String.format(KEY_FORMAT, lectureId, studentId);
    }

    public static String convertToKey(long lectureId, long studentId) {
        return String.format(KEY_FORMAT, lectureId, studentId);
    }
}
