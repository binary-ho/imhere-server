package gdsc.binaryho.imhere.core.lecture.infrastructure;

import gdsc.binaryho.imhere.config.redis.RedisKeyPrefixes;
import gdsc.binaryho.imhere.core.lecture.application.port.OpenLectureRepository;
import gdsc.binaryho.imhere.core.lecture.model.OpenLecture;
import gdsc.binaryho.imhere.core.lecture.model.OpenLectureFieldKeys;
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

        return Optional.of(OpenLecture.from(queryResult));
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

    private Map<String, String> getOpenLectureInfo(OpenLecture openLecture) {
        Map<String, String> hash = new HashMap<>();
        hash.put(OpenLectureFieldKeys.ID, String.valueOf(openLecture.getId()));
        hash.put(OpenLectureFieldKeys.NAME, openLecture.getLecturerName());
        hash.put(OpenLectureFieldKeys.ID, String.valueOf(openLecture.getId()));
        hash.put(OpenLectureFieldKeys.ID, String.valueOf(openLecture.getId()));
        return hash;
    }
}
