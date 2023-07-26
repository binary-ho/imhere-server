package gdsc.binaryho.imhere.core.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.fail;

import gdsc.binaryho.imhere.core.auth.application.AuthService;
import gdsc.binaryho.imhere.core.auth.application.port.VerificationCodeRepository;
import gdsc.binaryho.imhere.core.auth.exception.DuplicateEmailException;
import gdsc.binaryho.imhere.core.auth.exception.EmailVerificationCodeIncorrectException;
import gdsc.binaryho.imhere.core.auth.exception.MemberNotFoundException;
import gdsc.binaryho.imhere.core.auth.exception.PasswordFormatMismatchException;
import gdsc.binaryho.imhere.core.auth.exception.PasswordIncorrectException;
import gdsc.binaryho.imhere.core.auth.model.request.SignInRequest;
import gdsc.binaryho.imhere.core.auth.model.response.SignInRequestValidationResult;
import gdsc.binaryho.imhere.core.member.Member;
import gdsc.binaryho.imhere.core.member.infrastructure.MemberRepository;
import gdsc.binaryho.imhere.mock.FakeVerificationCodeRepository;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private AuthService authService;

    VerificationCodeRepository verificationCodeRepository = new FakeVerificationCodeRepository();

    private static final String UNIV_ID = "UNIV_ID";
    private static final String NAME = "이진호";
    private static final String PASSWORD = "abcd1234";
    private static final String DEFAULT_MEMBER_ROLE = "ROLE_STUDENT";
    private static final String EMAIL = "dlwlsgh4687@gmail.com";

    @BeforeEach
    void initAuthService() {
        authService = new AuthService(memberRepository, verificationCodeRepository, bCryptPasswordEncoder);
    }

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

    @Test
    void 인증_코드를_인증할_수_있다() {
        // given
        String verificationCode = "imhere forever";
        verificationCodeRepository.saveWithEmailAsKey(EMAIL, verificationCode);

        // when
        // then
        assertThatCode(
            () -> authService.verifyCode(EMAIL, verificationCode)
        ).doesNotThrowAnyException();
    }

    @Test
    void 인증_코드가_틀린_경우_예외를_발생시킨다() {
        // given
        String verificationCode = "imhere forever";
        verificationCodeRepository.saveWithEmailAsKey(EMAIL, verificationCode);

        // when
        // then
        assertThatThrownBy(
            () -> authService.verifyCode(EMAIL, verificationCode + "wrong code")
        ).isInstanceOf(EmailVerificationCodeIncorrectException.class);
    }
}
