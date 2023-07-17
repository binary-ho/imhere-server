package gdsc.binaryho.imhere.core.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import gdsc.binaryho.imhere.MockSecurityContextMember;
import gdsc.binaryho.imhere.core.auth.application.PrincipalDetailsService;
import gdsc.binaryho.imhere.core.member.Member;
import gdsc.binaryho.imhere.core.member.MemberRepository;
import gdsc.binaryho.imhere.core.member.Role;
import gdsc.binaryho.imhere.fixture.MemberFixture;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

@SpringBootTest
class PrincipalDetailsServiceTest {

    private static final String UNIV_ID = MemberFixture.UNIV_ID;
    private static final String NAME = MemberFixture.NAME;
    private static final String RAW_PASSWORD = MemberFixture.RAW_PASSWORD;
    private static final Role ROLE = MemberFixture.ROLE;

    @Autowired
    private PrincipalDetailsService principalDetailsService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @MockSecurityContextMember(univId = UNIV_ID)
    @Transactional
    void UnivId가_일치하는_유저의_PrincipalDetails을_생성할_수_있다() {

        Member member = Member.createMember(UNIV_ID, NAME, RAW_PASSWORD, ROLE);
        memberRepository.save(member);

        UserDetails userDetails = principalDetailsService.loadUserByUsername(UNIV_ID);
        UserDetails principalDetails = new PrincipalDetails(member);

        assertAll(
            () -> assertThat(userDetails.getUsername()).isEqualTo(principalDetails.getUsername()),
            () -> assertThat(userDetails.getPassword()).isEqualTo(principalDetails.getPassword())
        );
    }

    @Test
    void loadUserByUsername호출시_UnivId가_일치하는_유저가_없다면_예외를_던진다() {
        assertThatThrownBy(
            () -> principalDetailsService.loadUserByUsername("WRONG" + UNIV_ID)
        ).isInstanceOf(AuthenticationException.class);
    }
}
