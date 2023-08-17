package gdsc.binaryho.imhere.core.attendance.infrastructure;

import gdsc.binaryho.imhere.config.redis.RedisKeyPrefixes;
import gdsc.binaryho.imhere.core.attendance.application.port.AttendanceNumberRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AttendanceNumberRedisRepository implements AttendanceNumberRepository {

    private static final Integer ATTENDANCE_NUMBER_EXPIRE_TIME = 10;
    private static final String KEY_PREFIX = RedisKeyPrefixes.ATTENDANCE_NUMBER_KEY_PREFIX;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public Integer getByLectureId(Long lectureId) {
        String queryKey = KEY_PREFIX + lectureId;
        String attendanceNumber = redisTemplate.opsForValue()
            .get(queryKey);

        if (attendanceNumber == null) {
            return null;
        }

        return Integer.parseInt(attendanceNumber);
    }

    @Override
    public void saveWithLectureIdAsKey(Long lectureId, int attendanceNumber) {
        String saveKey = KEY_PREFIX + lectureId;

        redisTemplate.opsForValue().set(
            saveKey,
            String.valueOf(attendanceNumber),
            ATTENDANCE_NUMBER_EXPIRE_TIME,
            TimeUnit.MINUTES
        );
    }
}
