package gdsc.binaryho.imhere.api;

import gdsc.binaryho.imhere.mapper.requests.SignUpRequest;
import gdsc.binaryho.imhere.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// 비밀번호 변경도 필요
// 클라이언트에서 기본적으로 이메일 인증 이후라고 가정
@RestController
public class MemberController {

    private final MemberService memberService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public MemberController(
        MemberService memberService,
        BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.memberService = memberService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/member/new")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest signUpRequest) {
        try {
            String encodedPassword = bCryptPasswordEncoder.encode(signUpRequest.getPassword());

            memberService.signUp(signUpRequest.getUnivId(), signUpRequest.getName(), encodedPassword);
            return ResponseEntity.ok(HttpStatus.OK.getReasonPhrase());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
