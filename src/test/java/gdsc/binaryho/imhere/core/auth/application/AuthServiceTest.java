package gdsc.binaryho.imhere.core.auth.application;

import static gdsc.binaryho.imhere.mock.fixture.MemberFixture.MOCK_STUDENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import gdsc.binaryho.imhere.core.auth.application.port.VerificationCodeRepository;
import gdsc.binaryho.imhere.core.auth.exception.DuplicateEmailException;
import gdsc.binaryho.imhere.core.auth.exception.MemberNotFoundException;
import gdsc.binaryho.imhere.core.auth.exception.PasswordFormatMismatchException;
import gdsc.binaryho.imhere.core.auth.exception.PasswordIncorrectException;
import gdsc.binaryho.imhere.core.auth.model.request.SendPasswordChangeEmailRequest;
import gdsc.binaryho.imhere.core.auth.model.request.SendSignUpEmailRequest;
import gdsc.binaryho.imhere.core.auth.model.request.SignInRequest;
import gdsc.binaryho.imhere.core.auth.model.response.SignInRequestValidationResult;
import gdsc.binaryho.imhere.core.member.infrastructure.MemberRepository;
import gdsc.binaryho.imhere.mock.TestContainer;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AuthServiceTest {

    private static final String UNIV_ID = "UNIV_ID";
    private static final String EMAIL = "JinhoTest@gmail.com";
    private static final String NAME = "이진호";
    private static final String PASSWORD = "abcd1234";

    private AuthService authService;
    VerificationCodeRepository verificationCodeRepository;

    @Mock
    private MemberRepository memberRepository;

    TestContainer testContainer;

    @BeforeEach
    void beforeEachTest() {
        testContainer = TestContainer.builder()
            .memberRepository(memberRepository)
            .build();

        authService = testContainer.authService;
        verificationCodeRepository = testContainer.verificationCodeRepository;
    }

    @Test
    void 회원가입을_할_수_있다() {
        // given
        given(memberRepository.findByUnivId(UNIV_ID)).willReturn(Optional.empty());

        // when
        authService.signUp(UNIV_ID, NAME, PASSWORD);

        // then
        verify(memberRepository, times(1)).save(any());
    }

    @Test
    void 중복된_UnivId로_회원가입_시도시_중복_예외를_던진다() {
        given(memberRepository.findByUnivId(UNIV_ID)).willReturn(Optional.of(MOCK_STUDENT));

        // when
        // then
        assertThatThrownBy(() -> authService.signUp(UNIV_ID, NAME + "2", PASSWORD + "2"))
            .isInstanceOf(DuplicateEmailException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcdabcd", "12341234", "dafadfdafdafdfadfadfadfadafdaf", "a1"})
    void 회원가입시_비밀번호_형식이_잘못되면_예외를_던진다(String password) {
        // given
        given(memberRepository.findByUnivId(UNIV_ID)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> authService.signUp(UNIV_ID, NAME, password))
            .isInstanceOf(PasswordFormatMismatchException.class);
    }

    @Test
    void 로그인_요청_검증시_가입하지_않은_회원인_경우_예외를_던진다() {

        // given
        // when
        // then
        assertThatThrownBy(
            () -> authService.validateSignInRequest(new SignInRequest(UNIV_ID, PASSWORD))
        ).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 로그인_요청_검증시_비밀번호가_틀린_경우_예외를_던진다() {
        // given
        given(memberRepository.findByUnivId(UNIV_ID)).willReturn(Optional.of(MOCK_STUDENT));

        // when
        // then
        assertThatThrownBy(
            () -> authService.validateSignInRequest(new SignInRequest(UNIV_ID, PASSWORD + PASSWORD))
        ).isInstanceOf(PasswordIncorrectException.class);
    }

    @Test
    void 로그인_요청을_검증할_수_있다() {
        // given
        given(memberRepository.findByUnivId(UNIV_ID)).willReturn(Optional.of(MOCK_STUDENT));

        // when
        SignInRequestValidationResult signInRequestValidationResult =
            authService.validateSignInRequest(new SignInRequest(UNIV_ID, PASSWORD));

        // then
        assertThat(signInRequestValidationResult.getRoleKey()).isEqualTo(MOCK_STUDENT.getRoleKey());
    }

    @Test
    void 회원가입을_위한_인증_이메일_발송을_요청할_수_있다() {
        // given
        given(memberRepository.findByUnivId(EMAIL)).willReturn(Optional.empty());
        testContainer.isMailSent = false;

        // then
        authService.sendSignUpEmail(new SendSignUpEmailRequest(EMAIL));

        // then
        assertThat(testContainer.isMailSent).isTrue();
    }

    @Test
    void 비밀번호_변경을_위한_인증_이메일_발송을_요청할_수_있다() {
        // given
        given(memberRepository.findByUnivId(EMAIL)).willReturn(Optional.of(MOCK_STUDENT));
        testContainer.isMailSent = false;

        // then
        authService.sendPasswordChangeEmail(new SendPasswordChangeEmailRequest(EMAIL));

        // then
        assertThat(testContainer.isMailSent).isTrue();
    }

    @Test
    void 이미_가입한_이메일로_회원가입_이메일_발송_요청시_예와가_발생한다() {
        // given
        given(memberRepository.findByUnivId(EMAIL)).willReturn(Optional.of(MOCK_STUDENT));

        // then
        // then
        assertThatThrownBy(() ->
            authService.sendSignUpEmail(new SendSignUpEmailRequest(EMAIL)))
            .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void 가입하지_않은_회원이_비밀번호_변경을_위한_인증_이메일_발송_요청시_예와가_발생한다() {
        // given
        given(memberRepository.findByUnivId(EMAIL)).willReturn(Optional.empty());

        // then
        // then
        assertThatThrownBy(() ->
            authService.sendPasswordChangeEmail(new SendPasswordChangeEmailRequest(EMAIL)))
            .isInstanceOf(DuplicateEmailException.class);
    }
}
