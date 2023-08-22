package gdsc.binaryho.imhere.core.lecture.infrastructure;

import gdsc.binaryho.imhere.config.redis.RedisKeyPrefixes;
import gdsc.binaryho.imhere.core.lecture.application.port.AttendeeCacheRepository;
import gdsc.binaryho.imhere.core.lecture.model.StudentIds;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AttendeeCacheRedisRepository implements AttendeeCacheRepository {

    private static final int LECTURE_STUDENT_EXPIRE_TIME = 10;
    private static final String KEY_PREFIX = RedisKeyPrefixes.LECTURE_STUDENT_KEY_PREFIX;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public Set<Long> findAllAttendLectureId(Long studentId) {
        String queryKey = KEY_PREFIX + studentId.toString();
        DataType dataType = redisTemplate.type(queryKey);

        AttendeeFindStrategy readStrategy = AttendeeFindStrategy.fromDataType(dataType);
        return readStrategy.findLectureIds(redisTemplate, queryKey);
    }

    @Override
    public void cache(Long lectureIdValue, StudentIds studentIds) {
        final String lectureId = String.valueOf(lectureIdValue);

        studentIds
            .getStudentIds()
            .forEach(studentId -> cacheAttendee(studentId, lectureId));
    }

    private void cacheAttendee(Long studentId, String lectureId) {
        String key = KEY_PREFIX + studentId;
        DataType dataType = redisTemplate.type(key);
        AttendeeCacheStrategy saveStrategy = AttendeeCacheStrategy.fromDataType(dataType);
        saveStrategy.cache(redisTemplate, key, lectureId);
        redisTemplate.expire(key, LECTURE_STUDENT_EXPIRE_TIME, TimeUnit.MINUTES);
    }
}
