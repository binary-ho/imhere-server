package gdsc.binaryho.imhere.core.auth.infrastructure;

import static gdsc.binaryho.imhere.core.auth.util.SignUpEmailContents.MESSAGE_PREFIX;
import static gdsc.binaryho.imhere.core.auth.util.SignUpEmailContents.MESSAGE_SUBJECT;
import static gdsc.binaryho.imhere.core.auth.util.SignUpEmailContents.MESSAGE_SUFFIX;
import static gdsc.binaryho.imhere.core.auth.util.SignUpEmailContents.SENDER_ADDRESS;
import static gdsc.binaryho.imhere.core.auth.util.SignUpEmailContents.SENDER_PERSONAL;

import gdsc.binaryho.imhere.core.auth.application.port.MailSender;
import gdsc.binaryho.imhere.core.auth.exception.MessagingServerException;
import java.io.UnsupportedEncodingException;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class JavaMailSender implements MailSender {

    private final org.springframework.mail.javamail.JavaMailSender mailSender;
    private final StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void sendEmailWithVerificationCode(String recipient, String verificationCode) {
        try {
            MimeMessage message = writeMessage(recipient, verificationCode);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw MessagingServerException.EXCEPTION;
        }
    }

    private MimeMessage writeMessage(String recipient, String verificationCode)
        throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

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
}
