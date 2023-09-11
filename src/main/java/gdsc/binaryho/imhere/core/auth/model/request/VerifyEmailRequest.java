package gdsc.binaryho.imhere.core.auth.model.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class VerifyEmailRequest {
    private String email;
    private String verificationCode;
}