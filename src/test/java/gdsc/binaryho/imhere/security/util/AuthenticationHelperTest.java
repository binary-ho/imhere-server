package gdsc.binaryho.imhere.security.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;

import gdsc.binaryho.imhere.core.auth.exception.PermissionDeniedException;
import gdsc.binaryho.imhere.core.auth.exception.RequestMemberIdMismatchException;
import gdsc.binaryho.imhere.core.member.Member;
import gdsc.binaryho.imhere.core.member.Role;
import gdsc.binaryho.imhere.mock.securitycontext.MockSecurityContextMember;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AuthenticationHelperTest {

    private static final Long WRONG_ID = 0L;
    private final AuthenticationHelper authenticationHelper = new AuthenticationHelper();

    @Test
    @MockSecurityContextMember
    void 현재_로그인된_유저를_시큐리티_컨텍스트에서_가져올_수_있다() {
        try {
            Member actualLoginMember = authenticationHelper.getCurrentMember();
            assertThat(actualLoginMember).isNotNull();
        } catch (ParameterResolutionException e) {
            fail();
        }
    }

    @Test
    @MockSecurityContextMember
    void 현재_로그인된_유저가_입력된_아이디를_가졌는지_검증할_수_있다() {
        try {
            Member loginMember = authenticationHelper.getCurrentMember();
            authenticationHelper.verifyRequestMemberLogInMember(loginMember.getId());
        } catch (RequestMemberIdMismatchException e) {
            fail();
        }
    }

    @Test
    @MockSecurityContextMember
    void 현재_로그인된_유저와_입력된_아이디가_다른_경우_예외를_던진다() {

        assertThatThrownBy(
            () -> authenticationHelper.verifyRequestMemberLogInMember(WRONG_ID)
        ).isInstanceOf(RequestMemberIdMismatchException.class);
    }

    @Test
    @MockSecurityContextMember(role = Role.ADMIN)
    void 현재_로그인된_유저가_Admin인지_확인할_수_있다() {
        try {
            authenticationHelper.verifyMemberHasAdminRole();
        } catch (PermissionDeniedException e) {
            fail();
        }
    }

    @Test
    @MockSecurityContextMember(role = Role.LECTURER)
    void 현재_로그인된_유저가_Admin이_아닌_경우_예외를_던진다() {
        assertThatThrownBy(
            () -> authenticationHelper.verifyMemberHasAdminRole()
        ).isInstanceOf(PermissionDeniedException.class);
    }
}
