package gdsc.binaryho.imhere.domain.member;

import lombok.Getter;

@Getter
public class SignInResponseDto {

    private final String univId;
    private final String roleKey;

    public SignInResponseDto(String univId, String roleKey) {
        this.univId = univId;
        this.roleKey = roleKey;
    }
}
