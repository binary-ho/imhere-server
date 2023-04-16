package gdsc.binaryho.imhere.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.fail;

import gdsc.binaryho.imhere.domain.member.Member;
import gdsc.binaryho.imhere.domain.member.MemberRepository;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class MemberServiceTest {
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    String UNIV_ID = "UNIV_ID";
    String NAME = "이진호";
    String PASSWORD = "abcd1234";

    @Test
    @Transactional
    void 회원가입을_할_수_있다() {
        try {
            memberService.signUp(UNIV_ID, NAME, PASSWORD);
            Member member = memberRepository.findByUnivId(UNIV_ID).orElseThrow();
            assertAll(
                () -> assertThat(UNIV_ID).isEqualTo(UNIV_ID),
                () -> assertThat(member.getUnivId()).isEqualTo(UNIV_ID),
                () -> assertThat(member.getName()).isEqualTo(NAME),
                () -> assertThat(bCryptPasswordEncoder.matches(PASSWORD, member.getPassword())).isTrue()
            );
        } catch (RuntimeException e) {
            fail();
        }
    }

    @Test
    @Transactional
    void 중복된_UnivId로_회원가입_시도시_중복_예외를_던진다() {
        memberService.signUp(UNIV_ID, NAME, PASSWORD);
        assertThatThrownBy(() -> memberService.signUp(UNIV_ID, NAME + "2", PASSWORD + "2"))
            .isInstanceOf(DuplicateKeyException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcdabcd", "12341234", "dafadfdafdafdfadfadfadfadafdaf", "a1"})
    @Transactional
    void 회원가입시_비밀번호_형식이_잘못되면_예외를_던진다(String password) {
        assertThatThrownBy(() -> memberService.signUp(UNIV_ID, NAME, password))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
