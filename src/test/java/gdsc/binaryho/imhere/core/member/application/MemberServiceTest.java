package gdsc.binaryho.imhere.core.member.application;

import static gdsc.binaryho.imhere.mock.fixture.MemberFixture.UNIV_ID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import gdsc.binaryho.imhere.core.auth.exception.MemberNotFoundException;
import gdsc.binaryho.imhere.core.auth.exception.PermissionDeniedException;
import gdsc.binaryho.imhere.core.member.Member;
import gdsc.binaryho.imhere.core.member.Role;
import gdsc.binaryho.imhere.core.member.infrastructure.MemberRepository;
import gdsc.binaryho.imhere.core.member.model.request.ChangeRoleRequest;
import gdsc.binaryho.imhere.mock.securitycontext.MockSecurityContextMember;
import gdsc.binaryho.imhere.security.util.AuthenticationHelper;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest(roleKey);
        memberService.changeMemberRole(changeRoleRequest, UNIV_ID);

        // then
        verify(mockStudent, times(1)).setRole(Role.valueOf(roleKey));
    }

    @Test
    @MockSecurityContextMember(role = Role.STUDENT)
    void Admin이_아닌_경우_다른_회원의_권한_변경을_시도하면_예외가_발생한다() {
        // when
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest(Role.STUDENT.getKey());

        // then
        assertThatThrownBy(
            () ->  memberService.changeMemberRole(changeRoleRequest, UNIV_ID)
        ).isInstanceOf(PermissionDeniedException.class);
    }

    @Test
    @MockSecurityContextMember(role = Role.ADMIN)
    void 없는_회원의_권한_변경을_시도하면_예외가_발생한다() {
        given(memberRepository.findByUnivId(UNIV_ID)).willReturn(Optional.empty());

        // when
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest(Role.STUDENT.getKey());

        // then
        assertThatThrownBy(
            () ->  memberService.changeMemberRole(changeRoleRequest, UNIV_ID + 777L)
        ).isInstanceOf(MemberNotFoundException.class);
    }
}
