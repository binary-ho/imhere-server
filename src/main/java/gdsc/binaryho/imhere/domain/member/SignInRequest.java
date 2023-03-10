package gdsc.binaryho.imhere.domain.member;

import lombok.Getter;

@Getter
public class SignInRequest {
    private final String univId;
    private final String password;

    public SignInRequest(String univId, String password) {
        this.univId = univId;
        this.password = password;
    }
}
