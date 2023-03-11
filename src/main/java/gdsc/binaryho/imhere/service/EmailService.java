package gdsc.binaryho.imhere.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final static String MESSAGE_PREFIX = ""
        + "<div style='margin:20px;'>"
        + "<div style='margin:20px;'>"
        + "<h2> GDSC Hongik i'm here 입니다 </h1> <br>"
        + "<h3>회원가입 인증 코드입니다.</h3>"
        + "<div align='center' style='border:1px solid black; font-family:verdana';>"
        + "<div style='font-size:130%'>"
        + "CODE : <strong>";

    private final static String MESSAGE_SUFFIX = ""
        + "</strong><div><br/> "
        + "</div>"
        + "<p> 감사합니다. <p>"
        + "<br>";
    private final static int ATTENDANCE_NUMBER_EXPIRE_TIME = 10;
    private final static String EMAIL_REGEX = "^[a-zA-Z0-9]+@[g.]?hongik\\.ac\\.kr$";

    private final JavaMailSender emailSender;
    private final StringBuilder stringBuilder = new StringBuilder();
    private final RedisTemplate<String, String> redisTemplate;

    public void sendMailAndGetVerificationCode(String recipient) throws Exception {
        validateEmailForm(recipient);

        String verificationCode = UUID.randomUUID().toString();
        MimeMessage message = writeMail(recipient, verificationCode);

        try {
            emailSender.send(message);
            setVerificationCode(recipient, verificationCode);
        } catch (MailException e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    private MimeMessage writeMail(String recipient, String verificationCode) throws Exception {
        MimeMessage message = emailSender.createMimeMessage();

        message.addRecipients(RecipientType.TO, recipient);//보내는 대상
        message.setSubject("이메일 인증 테스트");//제목

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
        if (!recipient.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException();
        }
    }
}
