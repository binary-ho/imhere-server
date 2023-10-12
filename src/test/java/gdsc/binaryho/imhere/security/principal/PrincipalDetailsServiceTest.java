package gdsc.binaryho.imhere.security.principal;

import static gdsc.binaryho.imhere.mock.fixture.MemberFixture.MOCK_STUDENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import gdsc.binaryho.imhere.core.member.infrastructure.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
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
        given(memberRepository.findByUnivId("UNIV_ID")).willReturn(Optional.of(MOCK_STUDENT));

        UserDetails userDetails = principalDetailsService.loadUserByUsername("UNIV_ID");
        UserDetails principalDetails = new PrincipalDetails(MOCK_STUDENT);

        assertAll(
            () -> assertThat(userDetails.getUsername()).isEqualTo(principalDetails.getUsername()),
            () -> assertThat(userDetails.getPassword()).isEqualTo(principalDetails.getPassword())
        );
    }

    @Test
    void loadUserByUsername호출시_UnivId가_일치하는_유저가_없다면_예외를_던진다() {
        given(memberRepository.findByUnivId(any())).willReturn(Optional.empty());

        assertThatThrownBy(
            () -> principalDetailsService.loadUserByUsername("WRONG")
        ).isInstanceOf(AuthenticationException.class);
    }
}
