package gdsc.binaryho.imhere.core.auth.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignInRequest {

    @Schema(description = "회원가입 email에서 @와 도메인을 포함한 부분을 제외한 앞 부분")
    private String univId;
    private String password;
}
