package gdsc.binaryho.imhere.core.auth.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class SignInRequest {

    @Schema(description = "회원가입 email에서 @와 도메인을 포함한 부분을 제외한 앞 부분")
    private final String univId;
    private final String password;

    public SignInRequest(String univId, String password) {
        this.univId = univId;
        this.password = password;
    }
}
