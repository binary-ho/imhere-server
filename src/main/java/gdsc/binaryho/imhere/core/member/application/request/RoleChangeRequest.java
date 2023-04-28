package gdsc.binaryho.imhere.core.member.application.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoleChangeRequest {

    @Schema(description = "role을 string으로 표현합니다.", example = "ex) ROLE_STUDENT / ROLE_LECTURER / ROLE_ADMIN 과 같은 String")
    private String role;
}
