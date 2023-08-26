package gdsc.binaryho.imhere.mock.fakerepository;

import gdsc.binaryho.imhere.core.auth.application.port.VerificationCodeRepository;
import java.util.HashMap;
import java.util.Map;

public class FakeVerificationCodeRepository implements VerificationCodeRepository {

    private final Map<String, String> data = new HashMap<>();

    @Override
    public String getByEmail(String email) {
        return data.get(email);
    }

    @Override
    public void saveWithEmailAsKey(String email, String verificationCode) {
        data.put(email, verificationCode);
    }
}
