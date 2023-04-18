package gdsc.binaryho.imhere.api;

import gdsc.binaryho.imhere.mapper.requests.SignUpRequest;
import gdsc.binaryho.imhere.service.EmailService;
import gdsc.binaryho.imhere.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.UnsupportedEncodingException;
import javax.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "유저 계정 관련 API입니다.")
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    private final EmailService emailService;

    @Operation(summary = "회원가입 API")
    @PostMapping("/new")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest signUpRequest) {
        try {
            System.out.println("signUpRequest = " + signUpRequest.toString());
            memberService.signUp(signUpRequest.getUnivId(), signUpRequest.getName(),
                signUpRequest.getPassword());
            return ResponseEntity.ok(HttpStatus.OK.toString());
        } catch (RuntimeException error) {
            error.printStackTrace();
            return new ResponseEntity<>(error.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "특정 이메일로 회원가입 코드를 발급하여 발송하는 API")
    @PostMapping("/verification/{email}")
    public ResponseEntity<String> generateVerificationNumber(@PathVariable("email") String email) {
        try {
            emailService.sendMailAndGetVerificationCode(email);
            return ResponseEntity.ok(HttpStatus.OK.toString());
        } catch (MailException | MessagingException | UnsupportedEncodingException error) {
            error.printStackTrace();
            return new ResponseEntity<>(error.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "특정 이메일에 발급된 회원가입 코드와 입력된 코드의 일치여부를 확인하는 API")
    @GetMapping("/verification/{email}/{verification-code}")
    public boolean verifyCode(@PathVariable("email") String email,
        @PathVariable("verification-code") String verificationCode) {
        try {
            return emailService.verifyCode(email, verificationCode);
        } catch (RuntimeException e) {
            return false;
        }
    }
}
