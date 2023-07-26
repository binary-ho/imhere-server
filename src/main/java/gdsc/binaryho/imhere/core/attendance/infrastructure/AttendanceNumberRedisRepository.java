package gdsc.binaryho.imhere.core.attendance.infrastructure;

import gdsc.binaryho.imhere.core.attendance.application.port.AttendanceNumberRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AttendanceNumberRedisRepository implements AttendanceNumberRepository {

    private final static Integer ATTENDANCE_NUMBER_EXPIRE_TIME = 10;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public String getByLectureId(Long lectureId) {
        return redisTemplate.opsForValue()
            .get(lectureId.toString());
    }

    @Override
    public void saveByLectureId(Long lectureId, int attendanceNumber) {
        redisTemplate.opsForValue().set(
            lectureId.toString(),
            String.valueOf(attendanceNumber),
            ATTENDANCE_NUMBER_EXPIRE_TIME,
            TimeUnit.MINUTES
        );
    }
}
