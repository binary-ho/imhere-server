package gdsc.binaryho.imhere.core.auth.application;

import static gdsc.binaryho.imhere.fixture.MemberFixture.MOCK_STUDENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import gdsc.binaryho.imhere.core.auth.exception.DuplicateEmailException;
import gdsc.binaryho.imhere.core.auth.exception.MemberNotFoundException;
import gdsc.binaryho.imhere.core.auth.exception.PasswordFormatMismatchException;
import gdsc.binaryho.imhere.core.auth.exception.PasswordIncorrectException;
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
    private static final String NAME = "이진호";
    private static final String PASSWORD = "abcd1234";

    private AuthService authService;

    @Mock
    private MemberRepository memberRepository;

    TestContainer testContainer;

    @BeforeEach
    void beforeEachTest() {
        testContainer = TestContainer.builder()
            .memberRepository(memberRepository)
            .build();

        authService = testContainer.authService;
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
}
