package gdsc.binaryho.imhere.core.attendance.application;

import org.springframework.data.redis.core.RedisTemplate;

public enum AttendanceSaveRequestStatus {

    /*
    * 또한, 설문을 통해 유저들이 요청한 “출석 성공 조회”기능을 위해, 출석 요청, 성공, 예외 발생시 이벤트를 발행해 캐싱한다.
    * 출석 성공은 다른 상태를 덮어 쓰고, 다른 상태는 출석 성공을 덮어 쓸 수 없다.
    * */

    PROCESSING {
        @Override
        public void cache(
            RedisTemplate<String, String> redisTemplate, String key, String value) {
            String savedValue = redisTemplate.opsForValue().getAndDelete(key);
            if (SUCCESS.name().equals(savedValue))
            redisTemplate.opsForValue().set(key, value);
        }
    },

    SUCCESS {
        @Override
        public void cache(
            RedisTemplate<String, String> redisTemplate, String key, String value) {
            String savedValue = redisTemplate.opsForValue().getAndDelete(key);
            redisTemplate.opsForValue().set(key, savedValue, value);
        }
    },

    FAILED {
        @Override
        public void cache(
            RedisTemplate<String, String> redisTemplate, String key, String value) {
            redisTemplate.opsForSet().add(key, value);
        }
    },

    NO_REQUEST {
        @Override
        public void cache(
            RedisTemplate<String, String> redisTemplate, String key, String value) {
            redisTemplate.opsForSet().add(key, value);
        }
    };

    public abstract void cache(
        RedisTemplate<String, String> redisTemplate, String key, String value);

    public boolean canCache(String originalValue) {
        this.name()
    }
}
