package gdsc.binaryho.imhere.core.member;

import static gdsc.binaryho.imhere.fixture.MemberFixture.UNIV_ID;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import gdsc.binaryho.imhere.MockSecurityContextMember;
import gdsc.binaryho.imhere.core.auth.util.AuthenticationHelper;
import gdsc.binaryho.imhere.core.member.application.MemberService;
import gdsc.binaryho.imhere.core.member.infrastructure.MemberRepository;
import gdsc.binaryho.imhere.core.member.model.request.RoleChangeRequest;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MemberServiceTest {


    @Mock
    private MemberRepository memberRepository;
    private MemberService memberService;

    @BeforeEach
    void beforeEachTest() {
        memberService = new MemberService(new AuthenticationHelper(), memberRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"STUDENT", "LECTURER", "ADMIN"})
    @MockSecurityContextMember(role = Role.ADMIN)
    void Admin은_다른_회원의_권한을_변경할_수_있다(String roleKey) {
        Member mockStudent = mock(Member.class);
        given(memberRepository.findByUnivId(UNIV_ID)).willReturn(Optional.of(mockStudent));

        // when
        RoleChangeRequest roleChangeRequest = new RoleChangeRequest(roleKey);
        memberService.memberRoleChange(roleChangeRequest, UNIV_ID);

        // then
        verify(mockStudent, times(1)).setRole(Role.valueOf(roleKey));
    }

    // TODO : 예외 상황 테스트 작성
}
