package gdsc.binaryho.imhere.core.lecture.infrastructure;

import gdsc.binaryho.imhere.config.redis.RedisKeyPrefixes;
import gdsc.binaryho.imhere.core.lecture.application.port.OpenLectureRepository;
import gdsc.binaryho.imhere.core.lecture.model.OpenLecture;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OpenLectureRedisRepository implements OpenLectureRepository {

    private static final int OPEN_LECTURE_EXPIRE_TIME = 10;
    private static final String KEY_PREFIX = RedisKeyPrefixes.OPEN_LECTURE_KEY_PREFIX;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public Optional<OpenLecture> findByLectureId(Long lectureId) {
        String queryKey = KEY_PREFIX + lectureId;
        Map<Object, Object> queryResult = redisTemplate.opsForHash()
            .entries(queryKey);

        if (queryResult.isEmpty()) {
            return Optional.empty();
        }

        OpenLecture openLecture = getOpenLecture(lectureId, queryResult);
        return Optional.of(openLecture);
    }

    @Override
    public Integer findAttendanceNumber(Long lectureId) {
        return findByLectureId(lectureId)
            .map(OpenLecture::getAttendanceNumber)
            .orElse(null);
    }

    @Override
    public void save(OpenLecture openLecture) {
        String saveKey = KEY_PREFIX + openLecture;

        Map<String, String> openLectureInfo = getOpenLectureInfo(openLecture);
        redisTemplate.opsForHash()
            .putAll(saveKey, openLectureInfo);
        redisTemplate.expire(saveKey, OPEN_LECTURE_EXPIRE_TIME, TimeUnit.MINUTES);
    }

    private OpenLecture getOpenLecture(Long id, Map<Object, Object> queryResult) {
        String name = (String) queryResult.get(OpenLectureFieldKeys.NAME);
        String lecturerName = (String) queryResult.get(OpenLectureFieldKeys.LECTURER_NAME);
        int attendanceNumber = Integer.parseInt(
            (String) queryResult.get(OpenLectureFieldKeys.ATTENDANCE_NUMBER));

        return new OpenLecture(id, name, lecturerName, attendanceNumber);
    }

    private Map<String, String> getOpenLectureInfo(OpenLecture openLecture) {
        Map<String, String> hash = new HashMap<>();
        hash.put(OpenLectureFieldKeys.NAME, openLecture.getName());
        hash.put(OpenLectureFieldKeys.LECTURER_NAME, openLecture.getLecturerName());
        hash.put(OpenLectureFieldKeys.ATTENDANCE_NUMBER, String.valueOf(openLecture.getAttendanceNumber()));
        return hash;
    }

    private static class OpenLectureFieldKeys {

        private static final String NAME = "name";
        private static final String LECTURER_NAME = "lecturer_name";
        private static final String ATTENDANCE_NUMBER = "attendance_number";
    }
}
