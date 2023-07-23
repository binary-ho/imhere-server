package gdsc.binaryho.imhere.core.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.fail;

import gdsc.binaryho.imhere.MockSecurityContextMember;
import gdsc.binaryho.imhere.core.auth.application.AuthService;
import gdsc.binaryho.imhere.core.auth.model.response.SignInRequestValidationResult;
import gdsc.binaryho.imhere.core.auth.model.request.SignInRequest;
import gdsc.binaryho.imhere.core.auth.exception.DuplicateEmailException;
import gdsc.binaryho.imhere.core.auth.exception.MemberNotFoundException;
import gdsc.binaryho.imhere.core.auth.exception.PasswordFormatMismatchException;
import gdsc.binaryho.imhere.core.auth.exception.PasswordIncorrectException;
import gdsc.binaryho.imhere.core.member.Member;
import gdsc.binaryho.imhere.core.member.MemberRepository;
import gdsc.binaryho.imhere.core.member.Role;
import gdsc.binaryho.imhere.core.member.model.request.RoleChangeRequest;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class AuthServiceTest {
    @Autowired
    private AuthService authService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    String UNIV_ID = "UNIV_ID";
    String NAME = "이진호";
    String PASSWORD = "abcd1234";
    String DEFAULT_MEMBER_ROLE = "ROLE_STUDENT";

    @Test
    @Transactional
    void 회원가입을_할_수_있다() {

        try {
            authService.signUp(UNIV_ID, NAME, PASSWORD);

            Member member = memberRepository.findByUnivId(UNIV_ID)
                .orElseThrow();

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

        authService.signUp(UNIV_ID, NAME, PASSWORD);

        assertThatThrownBy(() -> authService.signUp(UNIV_ID, NAME + "2", PASSWORD + "2"))
            .isInstanceOf(DuplicateEmailException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcdabcd", "12341234", "dafadfdafdafdfadfadfadfadafdaf", "a1"})
    @Transactional
    void 회원가입시_비밀번호_형식이_잘못되면_예외를_던진다(String password) {

        assertThatThrownBy(() -> authService.signUp(UNIV_ID, NAME, password))
            .isInstanceOf(PasswordFormatMismatchException.class);
    }

    @Test
    void 로그인_요청_검증시_가입하지_않은_회원인_경우_예외를_던진다() {

        assertThatThrownBy(
            () -> authService.validateSignInRequest(new SignInRequest(UNIV_ID, PASSWORD))
        ).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @Transactional
    void 로그인_요청_검증시_비밀번호가_틀린_경우_예외를_던진다() {

        authService.signUp(UNIV_ID, NAME, PASSWORD);

        assertThatThrownBy(
            () -> authService.validateSignInRequest(new SignInRequest(UNIV_ID, PASSWORD + PASSWORD))
        ).isInstanceOf(PasswordIncorrectException.class);
    }

    @Test
    @Transactional
    void 로그인_요청을_검증할_수_있다() {

        authService.signUp(UNIV_ID, NAME, PASSWORD);

        SignInRequestValidationResult signInRequestValidationResult =
            authService.validateSignInRequest(new SignInRequest(UNIV_ID, PASSWORD));

        assertThat(signInRequestValidationResult.getRoleKey()).isEqualTo(DEFAULT_MEMBER_ROLE);
    }

    @ParameterizedTest
    @ValueSource(strings = {"STUDENT", "LECTURER", "ADMIN"})
    @MockSecurityContextMember(role = Role.ADMIN)
    @Transactional
    void test(String roleKey) {
        authService.signUp(UNIV_ID, NAME, PASSWORD);
        RoleChangeRequest roleChangeRequest = new RoleChangeRequest();
        roleChangeRequest.setRole(roleKey);

        authService.memberRoleChange(roleChangeRequest, UNIV_ID);
        Member member = memberRepository.findByUnivId(UNIV_ID)
            .orElseThrow();
        assertThat(member.getRole()).isEqualTo(Role.valueOf(roleKey));
    }
}
