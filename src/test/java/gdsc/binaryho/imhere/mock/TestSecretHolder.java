package gdsc.binaryho.imhere.mock;

import gdsc.binaryho.imhere.security.jwt.SecretHolder;

public class TestSecretHolder implements SecretHolder {

    private final String secret;

    public TestSecretHolder(String secret) {
        this.secret = secret;
    }

    @Override
    public String getSecret() {
        return secret;
    }
}
