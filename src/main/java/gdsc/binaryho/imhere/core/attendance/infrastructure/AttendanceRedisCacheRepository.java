package gdsc.binaryho.imhere.core.attendance.infrastructure;

import gdsc.binaryho.imhere.core.attendance.application.port.AttendanceHistoryCacheRepository;
import gdsc.binaryho.imhere.core.attendance.domain.AttendanceHistory;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Log4j2
@Repository
@RequiredArgsConstructor
public class AttendanceRedisCacheRepository implements AttendanceHistoryCacheRepository {

    private static final int ATTENDANCE_HISTORY_EXPIRE_HOUR = 1;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public List<AttendanceHistory> findAllByLectureIdAndStudentId(
        final long lectureId, final long studentId) {
        String key = AttendanceHistory.convertToKey(lectureId, studentId);

        return redisTemplate.opsForSet()
            .members(key)
            .stream()
            .map(timestamp -> AttendanceHistory.of(lectureId, studentId, timestamp))
            .collect(Collectors.toList());
    }

    @Override
    public void cache(AttendanceHistory attendanceHistory) {
        String key = attendanceHistory.getKey();
        redisTemplate.opsForSet()
            .add(key, attendanceHistory.getTimestamp());
        redisTemplate.expire(key, ATTENDANCE_HISTORY_EXPIRE_HOUR, TimeUnit.HOURS);
    }
}
