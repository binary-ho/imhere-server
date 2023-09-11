package gdsc.binaryho.imhere.core.auth.application;

import gdsc.binaryho.imhere.core.auth.application.port.MailSender;
import gdsc.binaryho.imhere.core.auth.application.port.VerificationCodeRepository;
import gdsc.binaryho.imhere.core.auth.exception.EmailVerificationCodeIncorrectException;
import gdsc.binaryho.imhere.core.auth.util.EmailFormValidator;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final MailSender mailSender;
    private final EmailFormValidator emailFormValidator;

    private final VerificationCodeRepository verificationCodeRepository;

    public void sendVerificationCodeByEmail(String recipient) {
        emailFormValidator.validateEmailForm(recipient);

        String verificationCode = UUID.randomUUID().toString();

        mailSender.sendEmailWithVerificationCode(recipient, verificationCode);
        saveVerificationCodeWithRecipientAsKey(recipient, verificationCode);

        log.info("[인증 이메일 발송] {}, 인증 번호 : {}",
            () -> recipient, () -> verificationCode);
    }

    private void saveVerificationCodeWithRecipientAsKey(String recipient, String verificationCode) {
        verificationCodeRepository.saveWithEmailAsKey(recipient, verificationCode);
    }

    @Transactional(readOnly = true)
    public void verifyCode(String email, String verificationCode) {
        String savedVerificationCode = verificationCodeRepository.getByEmail(email);
        if (!Objects.equals(savedVerificationCode, verificationCode)) {
            logEmailVerificationFail(email, verificationCode, savedVerificationCode);

            throw EmailVerificationCodeIncorrectException.EXCEPTION;
        }
        log.info("[이메일 인증 성공] email : {}", () -> email);
    }

    private void logEmailVerificationFail(String email, String verificationCode,
        String savedVerificationCode) {
        log.info("[이메일 인증 실패] email : {}, 제출 코드 : {}, 저장된 코드 : {}",
            () -> email, () -> verificationCode, () -> savedVerificationCode);
    }
}
