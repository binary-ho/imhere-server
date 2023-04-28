package gdsc.binaryho.imhere.security.jwt;

import lombok.Getter;

@Getter
public class Token {

    private final String accessToken;

    public Token(String accessToken) {
        this.accessToken = accessToken;
    }
}
