package gdsc.binaryho.imhere.core.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import gdsc.binaryho.imhere.core.auth.application.EmailVerificationService;
import gdsc.binaryho.imhere.core.auth.application.port.VerificationCodeRepository;
import gdsc.binaryho.imhere.core.auth.exception.EmailFormatMismatchException;
import gdsc.binaryho.imhere.core.auth.exception.EmailVerificationCodeIncorrectException;
import gdsc.binaryho.imhere.core.auth.exception.MessagingServerException;
import gdsc.binaryho.imhere.mock.FakeVerificationCodeRepository;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
public class EmailVerificationServiceTest {

    @Mock
    JavaMailSender javaMailSender;

    @Mock
    MimeMessage mockMimeMessage;

    VerificationCodeRepository verificationCodeRepository = new FakeVerificationCodeRepository();
    EmailVerificationService emailVerificationService;

    private static final String EMAIL = "dlwlsgh4687@gmail.com";

    @BeforeEach
    void initEmailSender() {
        emailVerificationService = new EmailVerificationService(javaMailSender, verificationCodeRepository);
    }

    @Test
    void 회원가임_시도_이메일에_메일을_보낼_수_있다() {
        // given
        given(javaMailSender.createMimeMessage()).willReturn(mockMimeMessage);
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        // then
        emailVerificationService.sendMailAndGetVerificationCode(EMAIL);

        // then
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
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
    void 메시지_기록중_MessagingException_이_발생하는_경우_커스텀_예외인_MessagingServerException_발생() throws MessagingException {
        // given
        // when
        given(javaMailSender.createMimeMessage()).willReturn(mockMimeMessage);
        doThrow(new MessagingException()).when(mockMimeMessage).addRecipients(RecipientType.TO, EMAIL);

        // then
        assertThatThrownBy(
            () -> emailVerificationService.sendMailAndGetVerificationCode(EMAIL)
        ).isInstanceOf(MessagingServerException.class);
    }

    @Test
    void 이메일_발송과_함께_인증_코드가_저장된다() {
        // given
        given(javaMailSender.createMimeMessage()).willReturn(mockMimeMessage);
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        // when
        emailVerificationService.sendMailAndGetVerificationCode(EMAIL);

        // then
        assertThat(verificationCodeRepository.getByEmail(EMAIL)).isNotNull();
    }

    @Test
    void 인증_코드를_인증할_수_있다() {
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
