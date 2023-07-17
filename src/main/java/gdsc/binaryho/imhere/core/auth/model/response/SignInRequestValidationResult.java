package gdsc.binaryho.imhere.core.auth.model.response;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;

@Getter
@Tag(name = "SignInRequestValidationResult", description = "로그인한 유저 id와 비밀번호를 검증한 다음 유저의 권한을 반환한다")
public class SignInRequestValidationResult {

    private final String roleKey;

    public SignInRequestValidationResult(String roleKey) {
        this.roleKey = roleKey;
    }
}
