package gdsc.binaryho.imhere.core.auth.application;

import gdsc.binaryho.imhere.core.auth.application.port.MailSender;
import gdsc.binaryho.imhere.core.auth.application.port.VerificationCodeRepository;
import gdsc.binaryho.imhere.core.auth.exception.EmailFormatMismatchException;
import gdsc.binaryho.imhere.core.auth.exception.EmailVerificationCodeIncorrectException;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9]+@(?:(?:g\\.)?hongik\\.ac\\.kr)$";
    private static final String GMAIL_REGEX = "^[a-zA-Z0-9]+@gmail\\.com$";

    private final MailSender mailSender;
    private final VerificationCodeRepository verificationCodeRepository;

    public void sendMailAndGetVerificationCode(String recipient) {
        validateEmailForm(recipient);

        String verificationCode = UUID.randomUUID().toString();

        mailSender.sendEmailWithVerificationCode(recipient, verificationCode);
        saveVerificationCodeWithRecipientAsKey(recipient, verificationCode);

        log.info("[인증 이메일 발송] " + recipient + ", 인증 번호 : " + verificationCode);
    }

    private void validateEmailForm(String recipient) {
        if (!recipient.matches(EMAIL_REGEX) && !recipient.matches(GMAIL_REGEX)) {
            throw EmailFormatMismatchException.EXCEPTION;
        }
    }

    private void saveVerificationCodeWithRecipientAsKey(String recipient, String verificationCode) {
        verificationCodeRepository.saveWithEmailAsKey(recipient, verificationCode);
    }

    public void verifyCode(String email, String verificationCode) {
        String emailVerificationCode = verificationCodeRepository.getByEmail(email);
        if (!Objects.equals(emailVerificationCode, verificationCode)) {
            throw EmailVerificationCodeIncorrectException.EXCEPTION;
        }
    }
}
