package gdsc.binaryho.imhere.core.auth.infrastructure;

import gdsc.binaryho.imhere.core.auth.application.port.VerificationCodeRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VerificationCodeRedisRepository implements VerificationCodeRepository {

    private static final Integer VERIFICATION_CODE_EXPIRE_TIME = 10;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public String getByEmail(String email) {
        if (email == null) {
            return null;
        }

        return redisTemplate.opsForValue().get(email);
    }

    @Override
    public void saveWithEmailAsKey(String email, String verificationCode) {
        redisTemplate.opsForValue().set(
            email,
            verificationCode,
            VERIFICATION_CODE_EXPIRE_TIME,
            TimeUnit.MINUTES
        );
    }
}
