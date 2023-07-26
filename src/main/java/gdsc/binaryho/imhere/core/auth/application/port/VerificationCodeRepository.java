package gdsc.binaryho.imhere.core.auth.application.port;

public interface VerificationCodeRepository {

    String getByEmail(String email);

    void saveWithEmailAsKey(String email, String verificationCode);
}
