package gdsc.binaryho.imhere.api;

import gdsc.binaryho.imhere.mapper.requests.SignUpRequest;
import gdsc.binaryho.imhere.service.EmailService;
import gdsc.binaryho.imhere.service.MemberService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final EmailService emailService;

    @PostMapping("/member/new")
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

    @PostMapping("/member/verification/{email}")
    public ResponseEntity<String> generateVerificationNumber(@PathVariable("email") String email) {
        try {
            emailService.sendMailAndGetVerificationCode(email);
            return ResponseEntity.ok(HttpStatus.OK.toString());
        } catch (MailException | MessagingException | UnsupportedEncodingException error) {
            error.printStackTrace();
            return new ResponseEntity<>(error.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/member/verification/{email}/{verification-code}")
    public boolean verifyCode(@PathVariable("email") String email,
        @PathVariable("verification-code") String verificationCode) {
        try {
            return emailService.verifyCode(email, verificationCode);
        } catch (RuntimeException e) {
            return false;
        }
    }
}
