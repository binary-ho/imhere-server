package gdsc.binaryho.imhere.core.auth.application;

import static gdsc.binaryho.imhere.fixture.MemberFixture.STUDENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

import gdsc.binaryho.imhere.core.auth.PrincipalDetails;
import gdsc.binaryho.imhere.core.member.infrastructure.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

@SpringBootTest
class PrincipalDetailsServiceTest {

    private PrincipalDetailsService principalDetailsService;

    @Mock
    private MemberRepository memberRepository;

    @BeforeEach
    void beforeEachTest() {
        principalDetailsService = new PrincipalDetailsService(memberRepository);
    }

    @Test
    void UnivId가_일치하는_유저의_PrincipalDetails을_생성할_수_있다() {
        given(memberRepository.findByUnivId("UNIV_ID")).willReturn(Optional.of(STUDENT));

        UserDetails userDetails = principalDetailsService.loadUserByUsername("UNIV_ID");
        UserDetails principalDetails = new PrincipalDetails(STUDENT);

        assertAll(
            () -> assertThat(userDetails.getUsername()).isEqualTo(principalDetails.getUsername()),
            () -> assertThat(userDetails.getPassword()).isEqualTo(principalDetails.getPassword())
        );
    }

    @Test
    void loadUserByUsername호출시_UnivId가_일치하는_유저가_없다면_예외를_던진다() {
        given(memberRepository.findByUnivId("UNIV_ID")).willReturn(Optional.empty());

        assertThatThrownBy(
            () -> principalDetailsService.loadUserByUsername("WRONG")
        ).isInstanceOf(AuthenticationException.class);
    }
}
