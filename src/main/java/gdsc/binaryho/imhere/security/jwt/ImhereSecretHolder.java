package gdsc.binaryho.imhere.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImhereSecretHolder implements SecretHolder {

    @Value("${jwt.access-token-prefix}")
    private String SECRET;

    @Override
    public String getSecret() {
        return SECRET;
    }
}
