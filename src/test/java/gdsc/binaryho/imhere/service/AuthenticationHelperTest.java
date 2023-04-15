package gdsc.binaryho.imhere.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import gdsc.binaryho.imhere.MockMember;
import gdsc.binaryho.imhere.domain.member.Member;
import gdsc.binaryho.imhere.domain.member.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthenticationHelperTest {

    @Autowired
    private AuthenticationHelper authenticationHelper;

    @Test
    @MockMember
    void 현재_로그인된_유저를_시큐리티_컨텍스트에서_가져올_수_있다() {
        try {
            Member actualLoginMember = authenticationHelper.getCurrentMember();
            assertThat(actualLoginMember).isNotNull();
        } catch (ParameterResolutionException e) {
            fail();
        }
    }

    @Test
    @MockMember
    void 현재_로그인된_유저가_입력된_아이디를_가졌는지_검증할_수_있다() {
        try {
            Member loginMember = authenticationHelper.getCurrentMember();
            authenticationHelper.verifyRequestMemberLogInMember(loginMember.getId());
        } catch (AccessDeniedException e) {
            fail();
        }
    }

    @Test
    @MockMember(role = Role.ADMIN)
    void 현재_로그인된_유저가_Admin인지_확인할_수_있다() {
        try {
            authenticationHelper.verifyMemberHasAdminRole();
        } catch (AccessDeniedException e) {
            fail();
        }
    }
}
