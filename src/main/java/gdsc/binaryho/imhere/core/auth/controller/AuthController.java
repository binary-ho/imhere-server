package gdsc.binaryho.imhere.core.auth.controller;

import gdsc.binaryho.imhere.core.auth.application.AuthService;
import gdsc.binaryho.imhere.core.auth.application.EmailVerificationService;
import gdsc.binaryho.imhere.core.auth.model.request.ChangePasswordRequest;
import gdsc.binaryho.imhere.core.auth.model.request.SendPasswordChangeEmailRequest;
import gdsc.binaryho.imhere.core.auth.model.request.SendSignUpEmailRequest;
import gdsc.binaryho.imhere.core.auth.model.request.SignUpRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@Tag(name = "Member", description = "유저 계정 관련 API입니다.")
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    private static final String TYPE = "type=";
    private static final String SIGN_UP = TYPE + "sign-up";
    private static final String PASSWORD_CHANGE = TYPE + "password-change";

    @Operation(summary = "회원가입 API")
    @PostMapping("/new")
    public ResponseEntity<Void> signUp(@RequestBody SignUpRequest signUpRequest) {
        authService.signUp(signUpRequest.getUnivId(), signUpRequest.getName(),
            signUpRequest.getPassword());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원가입을 위한 인증 코드와 이메일 발송 API", tags = SIGN_UP)
    @PostMapping(value = "/verification", params = SIGN_UP)
    public ResponseEntity<Void> sendSignUpEmail(
        @RequestBody SendSignUpEmailRequest sendSignUpEmailRequest) {
        authService.sendSignUpEmail(sendSignUpEmailRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비밀번호 변경을 위한 인증 코드와 이메일 발송 API", tags = PASSWORD_CHANGE)
    @PostMapping(value = "/verification", params = PASSWORD_CHANGE)
    public ResponseEntity<Void> sendPasswordChangeEmail(
        @RequestBody SendPasswordChangeEmailRequest sendPasswordChangeEmailRequest) {
        authService.sendPasswordChangeEmail(sendPasswordChangeEmailRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "특정 이메일에 발급된 회원가입 코드와 입력된 코드의 일치여부를 확인하는 API")
    @GetMapping("/verification/{email}/{verification-code}")
    public ResponseEntity<Void> verifyCode(@PathVariable("email") String email,
        @PathVariable("verification-code") String verificationCode) {
        emailVerificationService.verifyCode(email, verificationCode);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비밀번호 변경 API")
    @PostMapping("/password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        authService.changePassword(changePasswordRequest);
        return ResponseEntity.ok().build();
    }
}
