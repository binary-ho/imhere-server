package gdsc.binaryho.imhere.core.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import gdsc.binaryho.imhere.core.auth.application.EmailVerificationService;
import gdsc.binaryho.imhere.core.auth.application.port.MailSender;
import gdsc.binaryho.imhere.core.auth.application.port.VerificationCodeRepository;
import gdsc.binaryho.imhere.core.auth.exception.EmailFormatMismatchException;
import gdsc.binaryho.imhere.core.auth.exception.EmailVerificationCodeIncorrectException;
import gdsc.binaryho.imhere.mock.FakeVerificationCodeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class EmailVerificationServiceTest {

    boolean isMailSent;
    MailSender mailSender = (recipient, verificationCode) -> isMailSent = true;
    VerificationCodeRepository verificationCodeRepository = new FakeVerificationCodeRepository();

    EmailVerificationService emailVerificationService =
        new EmailVerificationService(mailSender, verificationCodeRepository);

    private static final String EMAIL = "dlwlsgh4687@gmail.com";

    @Test
    void 인증코드와_메일을_보낼_수_있다() {
        // given
        isMailSent = false;

        // then
        emailVerificationService.sendMailAndGetVerificationCode(EMAIL);

        // then
        assertThat(isMailSent).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"naver.com", "mail.hongik.ac.kr", "g.hongik.ac.k", "gmail.net"})
    void 홍익대학교_이메일이나_구글_이메일이_아닌_경우_예외_발생(String postfix) {
        // given
        // when
        // then
        assertThatThrownBy(() ->
            emailVerificationService.sendMailAndGetVerificationCode("test@" + postfix))
            .isInstanceOf(EmailFormatMismatchException.class);
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
