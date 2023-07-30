package gdsc.binaryho.imhere.mock;

import gdsc.binaryho.imhere.security.jwt.SecretHolder;

public class FakeSecretHolder implements SecretHolder {

    private final String secret;

    public FakeSecretHolder(String secret) {
        this.secret = secret;
    }

    @Override
    public String getSecret() {
        return secret;
    }
}
