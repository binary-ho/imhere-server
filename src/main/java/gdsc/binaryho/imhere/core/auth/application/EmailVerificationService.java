package gdsc.binaryho.imhere.core.auth.application;

import gdsc.binaryho.imhere.core.auth.application.port.VerificationCodeRepository;
import gdsc.binaryho.imhere.core.auth.exception.EmailFormatMismatchException;
import gdsc.binaryho.imhere.core.auth.exception.EmailVerificationCodeIncorrectException;
import gdsc.binaryho.imhere.core.auth.exception.MessagingServerException;
import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.UUID;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private static final String SENDER_PERSONAL = "jinholee";
    private static final String SENDER_ADDRESS = "gdscimhere@gmail.com";
    private static final String MESSAGE_SUBJECT = "Hello There! GDSC Hongik i'm here 회원가입 인증 코드입니다.";
    private final static String MESSAGE_PREFIX = ""
        + "<div style='margin:20px;'>"
        + "<div style='margin:20px;'>"
        + "<h1> GDSC Hongik i'm here 인증 코드 입니다! </h1> <br>"
        + "CODE : <strong>";

    private final static String MESSAGE_SUFFIX = ""
        + "</strong>"
        + "<h3> 10분안에 입력 부탁드립니다. 감사합니다! <h3>"
        + "<br>";
    private final static String EMAIL_REGEX = "^[a-zA-Z0-9]+@(?:(?:g\\.)?hongik\\.ac\\.kr)$";;
    private final static String GMAIL_REGEX = "^[a-zA-Z0-9]+@gmail\\.com$";
    private final StringBuilder stringBuilder = new StringBuilder();

    private final JavaMailSender emailSender;
    private final VerificationCodeRepository verificationCodeRepository;

    public void sendMailAndGetVerificationCode(String recipient) {
        validateEmailForm(recipient);

        String verificationCode = UUID.randomUUID().toString();

        try {
            MimeMessage message = writeMessage(recipient, verificationCode);
            emailSender.send(message);
        } catch (MessagingException e) {
            throw MessagingServerException.EXCEPTION;
        }

        setVerificationCode(recipient, verificationCode);

        log.info("[인증 이메일 발송] " + recipient + ", 인증 번호 : " + verificationCode);
    }

    private MimeMessage writeMessage(String recipient, String verificationCode)
        throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();

        message.addRecipients(RecipientType.TO, recipient);
        message.setSubject(MESSAGE_SUBJECT);
        message.setText(getMessage(verificationCode), "utf-8", "html");
        message.setFrom(getInternetAddress());
        return message;
    }

    private String getMessage(String verificationCode) {
        stringBuilder.setLength(0);
        return String.valueOf(stringBuilder.append(MESSAGE_PREFIX)
            .append(verificationCode)
            .append(MESSAGE_SUFFIX));
    }

    private InternetAddress getInternetAddress() {
        try {
            return new InternetAddress(SENDER_ADDRESS, SENDER_PERSONAL);
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            log.error("[UnsupportedEncodingException] address : {}, personal : {}",
                SENDER_ADDRESS, SENDER_PERSONAL);
            return new InternetAddress();
        }
    }

    private void setVerificationCode(String recipient, String verificationCode) {
        verificationCodeRepository.saveWithEmailAsKey(recipient, verificationCode);
    }

    private void validateEmailForm(String recipient) {
        if (!recipient.matches(EMAIL_REGEX) && !recipient.matches(GMAIL_REGEX)) {
            throw EmailFormatMismatchException.EXCEPTION;
        }
    }

    public void verifyCode(String email, String verificationCode) {
        String emailVerificationCode = verificationCodeRepository.getByEmail(email);
        if (!Objects.equals(emailVerificationCode, verificationCode)) {
            throw EmailVerificationCodeIncorrectException.EXCEPTION;
        }
    }
}
