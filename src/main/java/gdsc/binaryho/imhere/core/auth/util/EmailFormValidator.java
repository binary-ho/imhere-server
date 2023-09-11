package gdsc.binaryho.imhere.core.auth.util;

import gdsc.binaryho.imhere.core.auth.exception.EmailFormatMismatchException;
import org.springframework.stereotype.Component;

@Component
public class EmailFormValidator {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9]+@(?:(?:g\\.)?hongik\\.ac\\.kr)$";
    private static final String GMAIL_REGEX = "^[a-zA-Z0-9]+@gmail\\.com$";

    public void validateEmailForm(String recipient) {
        if (!recipient.matches(EMAIL_REGEX) && !recipient.matches(GMAIL_REGEX)) {
            throw EmailFormatMismatchException.EXCEPTION;
        }
    }
}
