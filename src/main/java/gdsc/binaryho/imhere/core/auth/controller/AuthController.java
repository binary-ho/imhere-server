package gdsc.binaryho.imhere.core.auth.controller;

import gdsc.binaryho.imhere.core.auth.application.AuthService;
import gdsc.binaryho.imhere.core.auth.model.request.SignUpRequest;
import gdsc.binaryho.imhere.core.auth.util.EmailSender;
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
    private final EmailSender emailSender;

    @Operation(summary = "회원가입 API")
    @PostMapping("/new")
    public ResponseEntity<Void> signUp(@RequestBody SignUpRequest signUpRequest) {
        authService.signUp(signUpRequest.getUnivId(), signUpRequest.getName(),
            signUpRequest.getPassword());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "특정 이메일로 회원가입 코드를 발급하여 발송하는 API")
    @PostMapping("/verification/{email}")
    public ResponseEntity<Void> generateVerificationNumber(@PathVariable("email") String email) {
        emailSender.sendMailAndGetVerificationCode(email);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "특정 이메일에 발급된 회원가입 코드와 입력된 코드의 일치여부를 확인하는 API")
    @GetMapping("/verification/{email}/{verification-code}")
    public ResponseEntity<Void> verifyCode(@PathVariable("email") String email,
        @PathVariable("verification-code") String verificationCode) {
        emailSender.verifyCode(email, verificationCode);
        return ResponseEntity.ok().build();
    }
}
