package gdsc.binaryho.imhere.service;

import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final static String MESSAGE_PREFIX = ""
        + "<div style='margin:20px;'>"
        + "<div style='margin:20px;'>"
        + "<h1> GDSC Hongik i'm here 인증 코드 입니다! </h1> <br>"
        + "CODE : <strong>";

    private final static String MESSAGE_SUFFIX = ""
        + "</strong>"
        + "<h3> 10분안에 입력 부탁드립니다. 감사합니다! <h3>"
        + "<br>";
    private final static int ATTENDANCE_NUMBER_EXPIRE_TIME = 10;
    private final static String EMAIL_REGEX = "^[a-zA-Z0-9]+@(?:(?:g\\.)?hongik\\.ac\\.kr)$";;
    private final static String GMAIL_REGEX = "^[a-zA-Z0-9]+@gmail\\.com$";    

    private final JavaMailSender emailSender;
    private final StringBuilder stringBuilder = new StringBuilder();
    private final RedisTemplate<String, String> redisTemplate;

    public void sendMailAndGetVerificationCode(String recipient)
        throws MessagingException, UnsupportedEncodingException {
        validateEmailForm(recipient);

        String verificationCode = UUID.randomUUID().toString();

        MimeMessage message = writeMail(recipient, verificationCode);
        emailSender.send(message);
        setVerificationCode(recipient, verificationCode);
    }

    private MimeMessage writeMail(String recipient, String verificationCode)
        throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = emailSender.createMimeMessage();

        message.addRecipients(RecipientType.TO, recipient);
        message.setSubject("Hello There! GDSC Hongik i'm here 회원가입 인증 코드입니다.");

        message.setText(getMessage(verificationCode), "utf-8", "html");//내용
        message.setFrom(new InternetAddress("gdscimhere@gmail.com", "jinholee"));//보내는 사람
        return message;
    }

    private String getMessage(String verificationCode) {
        stringBuilder.setLength(0);
        return String.valueOf(stringBuilder.append(MESSAGE_PREFIX)
            .append(verificationCode)
            .append(MESSAGE_SUFFIX));
    }

    private void setVerificationCode(String recipient, String verificationCode) {
        redisTemplate.opsForValue().set(
            recipient,
            verificationCode,
            ATTENDANCE_NUMBER_EXPIRE_TIME,
            TimeUnit.MINUTES
        );
    }

    private void validateEmailForm(String recipient) {
        if (!recipient.matches(EMAIL_REGEX) && !recipient.matches(GMAIL_REGEX)) {
            throw new IllegalArgumentException();
        }
    }

    public boolean verifyCode(String email, String verificationCode) {
        String nonNullEmail = Objects.requireNonNull(email);
        String nonNullVerificationCode = Objects.requireNonNull(verificationCode);
        return Objects.equals(redisTemplate.opsForValue().get(nonNullEmail), nonNullVerificationCode);
    }
}
