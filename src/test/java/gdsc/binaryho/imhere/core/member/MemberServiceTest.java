package gdsc.binaryho.imhere.core.member;

import static org.assertj.core.api.Assertions.assertThat;

import gdsc.binaryho.imhere.MockMember;
import gdsc.binaryho.imhere.core.auth.application.AuthService;
import gdsc.binaryho.imhere.core.member.application.MemberService;
import gdsc.binaryho.imhere.core.member.model.request.RoleChangeRequest;
import javax.transaction.Transactional;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    String UNIV_ID = "UNIV_ID";
    String NAME = "이진호";
    String PASSWORD = "abcd1234";

    @ParameterizedTest
    @ValueSource(strings = {"STUDENT", "LECTURER", "ADMIN"})
    @MockMember(role = Role.ADMIN)
    @Transactional
    void test(String roleKey) {
        authService.signUp(UNIV_ID, NAME, PASSWORD);
        RoleChangeRequest roleChangeRequest = new RoleChangeRequest(roleKey);

        memberService.memberRoleChange(roleChangeRequest, UNIV_ID);
        Member member = memberRepository.findByUnivId(UNIV_ID)
            .orElseThrow();
        assertThat(member.getRole()).isEqualTo(Role.valueOf(roleKey));
    }
}
