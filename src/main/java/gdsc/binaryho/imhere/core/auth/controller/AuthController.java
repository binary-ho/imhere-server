package gdsc.binaryho.imhere.core.auth.controller;

import gdsc.binaryho.imhere.core.auth.application.AuthService;
import gdsc.binaryho.imhere.core.auth.application.request.SignUpRequest;
import gdsc.binaryho.imhere.exception.ImhereException;
import gdsc.binaryho.imhere.util.EmailSender;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.UnsupportedEncodingException;
import javax.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
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
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest signUpRequest) {
        try {
            authService.signUp(signUpRequest.getUnivId(), signUpRequest.getName(),
                signUpRequest.getPassword());
            return ResponseEntity.ok(HttpStatus.OK.toString());
        } catch (ImhereException e) {
            log.info("[회원가입 에러] 제출 email : {}, name : {}, password : {}\n -> 사유 {}",
                () -> signUpRequest.getUnivId(),
                () -> signUpRequest.getName(),
                () -> signUpRequest.getPassword(),
                () -> e.getErrorCode().getMessage());
            return ResponseEntity.status(e.getErrorCode().getCode()).build();
        }
    }

    @Operation(summary = "특정 이메일로 회원가입 코드를 발급하여 발송하는 API")
    @PostMapping("/verification/{email}")
    public ResponseEntity<String> generateVerificationNumber(@PathVariable("email") String email) {
        try {
            emailSender.sendMailAndGetVerificationCode(email);
            return ResponseEntity.ok(HttpStatus.OK.toString());
        } catch (MailException | MessagingException | UnsupportedEncodingException error) {
            log.info("[이메일 인증 번호 전송 에러] email : {}", email);
            return new ResponseEntity<>(error.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "특정 이메일에 발급된 회원가입 코드와 입력된 코드의 일치여부를 확인하는 API")
    @GetMapping("/verification/{email}/{verification-code}")
    public boolean verifyCode(@PathVariable("email") String email,
        @PathVariable("verification-code") String verificationCode) {
        try {
            return emailSender.verifyCode(email, verificationCode);
        } catch (RuntimeException e) {
            log.info("[이메일 인증 번호 불일치] email : {}, 제출 코드 {}", email, verificationCode);
            return false;
        }
    }
}