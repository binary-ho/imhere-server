package gdsc.binaryho.imhere.service;

import static org.assertj.core.api.Assertions.assertThat;

import gdsc.binaryho.imhere.domain.member.Member;
import gdsc.binaryho.imhere.domain.member.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthenticationHelperTest {

    @Autowired
    private AuthenticationHelper authenticationHelper;

    @Test
    @WithMockUser(username = "id", roles = {"STUDENT"})
    void 현재_로그인된_유저를_시큐리티_컨텍스트에서_가져올_수_있다() {
        String mockId = "id";
        String mockName = "name";
        String mockPassword = "password";
        Member mockMember = Member.createMember(mockId, mockName, mockPassword, Role.STUDENT);
        Member actualLoginMember = authenticationHelper.getCurrentMember();
        assertThat(actualLoginMember.getId()).isEqualTo(mockMember.getId());
    }
}
