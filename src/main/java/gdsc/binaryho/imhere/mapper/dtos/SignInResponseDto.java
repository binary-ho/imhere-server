package gdsc.binaryho.imhere.mapper.dtos;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;

@Getter
@Tag(name = "SignInResponseDto", description = "로그인한 유저 id와 권한")
public class SignInResponseDto {

    private final String univId;
    private final String roleKey;

    public SignInResponseDto(String univId, String roleKey) {
        this.univId = univId;
        this.roleKey = roleKey;
    }
}
