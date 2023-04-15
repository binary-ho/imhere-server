package gdsc.binaryho.imhere.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import gdsc.binaryho.imhere.MockMember;
import gdsc.binaryho.imhere.domain.member.Member;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
}
