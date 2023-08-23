package gdsc.binaryho.imhere.core.lecture.infrastructure;

import gdsc.binaryho.imhere.core.lecture.exception.UnexpectedRedisDataTypeException;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;

public enum AttendeeCacheStrategy {

    SAVE_AS_STRING(DataType.NONE) {
        @Override
        public void cache(
            RedisTemplate<String, String> redisTemplate, String key, String value) {
            redisTemplate.opsForValue().set(key, value);
        }
    },

    CONVERT_TO_SET(DataType.STRING) {
        @Override
        public void cache(
            RedisTemplate<String, String> redisTemplate, String key, String value) {
            String savedValue = redisTemplate.opsForValue().get(key);
            redisTemplate.delete(key);
            redisTemplate.opsForSet().add(key, savedValue, value);
        }
    },

    ADD_TO_SET(DataType.SET) {
        @Override
        public void cache(
            RedisTemplate<String, String> redisTemplate, String key, String value) {
            redisTemplate.opsForSet().add(key, value);
        }
    };

    private final DataType dataType;

    public abstract void cache(
        RedisTemplate<String, String> redisTemplate, String key, String value);

    public static AttendeeCacheStrategy fromDataType(DataType dataType) {
        for (AttendeeCacheStrategy strategy : values()) {
            if (strategy.dataType == dataType) {
                return strategy;
            }
        }

        throw UnexpectedRedisDataTypeException.EXCEPTION;
    }

    AttendeeCacheStrategy(DataType dataType) {
        this.dataType = dataType;
    }
}
