package gdsc.binaryho.imhere.domain.member;

import lombok.Getter;

@Getter
public class SignUpRequest {

    private final String univId;
    private final String name;
    private final String password;

    public SignUpRequest(String univId, String name, String password) {
        this.univId = univId;
        this.name = name;
        this.password = password;
    }
}
