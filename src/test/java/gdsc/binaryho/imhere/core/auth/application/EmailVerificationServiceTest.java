package gdsc.binaryho.imhere.core.auth.application;

import static gdsc.binaryho.imhere.mock.fixture.MemberFixture.MOCK_STUDENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import gdsc.binaryho.imhere.core.auth.application.port.VerificationCodeRepository;
import gdsc.binaryho.imhere.core.auth.exception.DuplicateEmailException;
import gdsc.binaryho.imhere.core.auth.exception.EmailFormatMismatchException;
import gdsc.binaryho.imhere.core.auth.exception.EmailVerificationCodeIncorrectException;
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
public class EmailVerificationServiceTest {

    private static final String EMAIL = "dlwlsgh4687@gmail.com";

    EmailVerificationService emailVerificationService;
    VerificationCodeRepository verificationCodeRepository;

    @Mock
    private MemberRepository memberRepository;

    TestContainer testContainer;

    @BeforeEach
    void beforeEachTest() {
        testContainer = TestContainer.builder()
            .memberRepository(memberRepository)
            .build();

        verificationCodeRepository = testContainer.verificationCodeRepository;
        emailVerificationService = testContainer.emailVerificationService;
    }

    @Test
    void 인증코드와_메일을_보낼_수_있다() {
        // given
        testContainer.isMailSent = false;

        // then
        emailVerificationService.sendMailAndGetVerificationCode(EMAIL);

        // then
        assertThat(testContainer.isMailSent).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"naver.com", "mail.hongik.ac.kr", "g.hongik.ac.k", "gmail.net"})
    void 홍익대학교_이메일이나_구글_이메일이_아닌_경우_예외가_발생한다(String postfix) {
        // given
        // when
        // then
        assertThatThrownBy(() ->
            emailVerificationService.sendMailAndGetVerificationCode("test@" + postfix))
            .isInstanceOf(EmailFormatMismatchException.class);
    }

    @Test
    void 이미_가입한_이메일로_인증_요청시_예와가_발생한다() {
        // given
        given(memberRepository.findByUnivId(EMAIL)).willReturn(Optional.of(MOCK_STUDENT));

        // then
        // then
        assertThatThrownBy(() ->
            emailVerificationService.sendMailAndGetVerificationCode(EMAIL))
            .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void 이메일_발송과_함께_인증_코드가_저장된다() {
        // given
        // when
        emailVerificationService.sendMailAndGetVerificationCode(EMAIL);

        // then
        assertThat(verificationCodeRepository.getByEmail(EMAIL)).isNotNull();
    }

    @Test
    void 인증_코드를_검증할_수_있다() {
        // given
        String verificationCode = "imhere forever";
        verificationCodeRepository.saveWithEmailAsKey(EMAIL, verificationCode);

        // when
        // then
        assertThatCode(
            () -> emailVerificationService.verifyCode(EMAIL, verificationCode)
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
            () -> emailVerificationService.verifyCode(EMAIL, verificationCode + "wrong code")
        ).isInstanceOf(EmailVerificationCodeIncorrectException.class);
    }
}
