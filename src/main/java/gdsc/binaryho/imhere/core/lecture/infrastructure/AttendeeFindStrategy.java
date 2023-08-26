package gdsc.binaryho.imhere.core.lecture.infrastructure;

import gdsc.binaryho.imhere.core.lecture.exception.UnexpectedRedisDataTypeException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;

public enum AttendeeFindStrategy {

    EMPTY(DataType.NONE) {
        @Override
        public Set<Long> findLectureIds(RedisTemplate<String, String> redisTemplate, String key) {
            return Collections.emptySet();
        }
    },

    ONLY_ONE(DataType.STRING) {
        @Override
        public Set<Long> findLectureIds(RedisTemplate<String, String> redisTemplate, String key) {
            String value = redisTemplate.opsForValue().get(key);
            return Collections.singleton(Long.parseLong(value));
        }
    },

    TWO_OR_MORE(DataType.SET) {
        @Override
        public Set<Long> findLectureIds(RedisTemplate<String, String> redisTemplate, String key) {
            Set<String> queryResult = redisTemplate.opsForSet().members(key);
            return queryResult.stream()
                .map(Long::parseLong)
                .collect(Collectors.toSet());
        }
    };

    private final DataType dataType;

    public abstract Set<Long> findLectureIds(RedisTemplate<String, String> redisTemplate, String key);

    public static AttendeeFindStrategy fromDataType(DataType dataType) {
        return Arrays
            .stream(values())
            .filter(strategy -> strategy.dataType == dataType)
            .findAny()
            .orElseThrow(() -> UnexpectedRedisDataTypeException.EXCEPTION);
    }

    AttendeeFindStrategy(DataType dataType) {
        this.dataType = dataType;
    }
}
