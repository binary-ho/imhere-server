package gdsc.binaryho.imhere.security.jwt;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImhereTokenPropertyHolder implements TokenPropertyHolder {

    @Value("${token.secret}")
    private String secret;

    @Value("${token.access-token-expire-minute}")
    private Integer accessTokenExpireMinute;

    @Value("${token.access-token-prefix}")
    private String accessTokenPrefix;

    @Override
    public String getSecret() {
        return secret;
    }

    @Override
    public Duration getAccessTokenExpiration() {
        return Duration.ofMinutes(accessTokenExpireMinute);
    }

    @Override
    public String getAccessTokenPrefix() {
        return accessTokenPrefix;
    }
}
