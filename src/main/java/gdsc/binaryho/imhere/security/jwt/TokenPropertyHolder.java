package gdsc.binaryho.imhere.security.jwt;

import java.time.Duration;

public interface TokenPropertyHolder {

    String getSecret();

    Duration getAccessTokenExpiration();

    String getAccessTokenPrefix();
}
