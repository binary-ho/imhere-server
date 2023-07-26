package gdsc.binaryho.imhere.core.auth.application.port;

public interface MailSender {

    void sendEmailWithVerificationCode(String recipient, String verificationCode);
}
