package gdsc.binaryho.imhere.core.auth.infrastructure;

import gdsc.binaryho.imhere.config.redis.RedisKeyPrefixes;
import gdsc.binaryho.imhere.core.auth.application.port.VerificationCodeRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VerificationCodeRedisRepository implements VerificationCodeRepository {

    private static final Integer VERIFICATION_CODE_EXPIRE_TIME = 10;
    private final String KEY_PREFIX = RedisKeyPrefixes.VERIFICATION_CODE_KEY_PREFIX;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public String getByEmail(String email) {
        String queryKey = KEY_PREFIX + email;
        return redisTemplate.opsForValue().get(queryKey);
    }

    @Override
    public void saveWithEmailAsKey(String email, String verificationCode) {
        String saveKey = KEY_PREFIX + email;

        redisTemplate.opsForValue().set(
            saveKey,
            verificationCode,
            VERIFICATION_CODE_EXPIRE_TIME,
            TimeUnit.MINUTES
        );
    }
}
