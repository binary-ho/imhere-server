package gdsc.binaryho.imhere.security;

import gdsc.binaryho.imhere.security.jwt.TokenPropertyHolder;
import java.time.Duration;

public class FakeTokenPropertyHolder implements TokenPropertyHolder {

    private final String secret;
    private final Duration accessTokenExpiration;
    private final String accessTokenPrefix;


    public FakeTokenPropertyHolder(String secret, Duration accessTokenExpiration, String accessTokenPrefix) {
        this.secret = secret;
        this.accessTokenExpiration = accessTokenExpiration;
        this.accessTokenPrefix = accessTokenPrefix;
    }

    @Override
    public String getSecret() {
        return secret;
    }

    @Override
    public Duration getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    @Override
    public String getAccessTokenPrefix() {
        return accessTokenPrefix;
    }
}
