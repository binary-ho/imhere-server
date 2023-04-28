package gdsc.binaryho.imhere.core.auth.application.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class SignUpRequest {

    @Schema(description = "회원가입 email에서 @와 도메인을 포함한 부분을 제외한 앞 부분")
    private final String univId;
    private final String name;
    private final String password;

    public SignUpRequest(String univId, String name, String password) {
        this.univId = univId;
        this.name = name;
        this.password = password;
    }
}
