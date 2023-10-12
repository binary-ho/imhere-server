package gdsc.binaryho.imhere.core.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import gdsc.binaryho.imhere.core.auth.application.port.VerificationCodeRepository;
import gdsc.binaryho.imhere.core.auth.exception.EmailVerificationCodeIncorrectException;
import gdsc.binaryho.imhere.core.member.infrastructure.MemberRepository;
import gdsc.binaryho.imhere.mock.TestContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

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
        emailVerificationService.sendVerificationCodeByEmail(EMAIL);

        // then
        assertThat(testContainer.isMailSent).isTrue();
    }

    @Test
    void 이메일_발송과_함께_인증_코드가_저장된다() {
        // given
        // when
        emailVerificationService.sendVerificationCodeByEmail(EMAIL);

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
