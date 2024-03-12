package gdsc.binaryho.imhere.mock;

import gdsc.binaryho.imhere.security.jwt.TokenPropertyHolder;
import java.time.Duration;

public class FakeTokenPropertyHolder implements TokenPropertyHolder {

    private final String secret;
    private final Duration accessTokenExpiration;


    public FakeTokenPropertyHolder(String secret, Duration accessTokenExpiration) {
        this.secret = secret;
        this.accessTokenExpiration = accessTokenExpiration;
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
        return "prefix";
    }
}
