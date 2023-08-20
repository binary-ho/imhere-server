package gdsc.binaryho.imhere.core.lecture.infrastructure;

import gdsc.binaryho.imhere.core.lecture.exception.UnexpectedRedisDataTypeException;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;

public enum StudentSaveStrategy {

    SAVE_AS_STRING(DataType.NONE) {
        @Override
        public void saveStudent(
            RedisTemplate<String, String> redisTemplate, String key, String value) {
            redisTemplate.opsForValue().set(key, value);
        }
    },

    CONVERT_TO_SET(DataType.STRING) {
        @Override
        public void saveStudent(
            RedisTemplate<String, String> redisTemplate, String key, String value) {
            String savedValue = redisTemplate.opsForValue().get(key);
            redisTemplate.delete(key);
            redisTemplate.opsForSet().add(key, savedValue, value);
        }
    },

    ADD_TO_SET(DataType.SET) {
        @Override
        public void saveStudent(
            RedisTemplate<String, String> redisTemplate, String key, String value) {
            redisTemplate.opsForSet().add(key, value);
        }
    };

    private final DataType dataType;

    public abstract void saveStudent(
        RedisTemplate<String, String> redisTemplate, String key, String value);

    public static StudentSaveStrategy fromDataType(DataType dataType) {
        for (StudentSaveStrategy strategy : values()) {
            if (strategy.dataType == dataType) {
                return strategy;
            }
        }

        throw UnexpectedRedisDataTypeException.EXCEPTION;
    }

    StudentSaveStrategy(DataType dataType) {
        this.dataType = dataType;
    }
}
