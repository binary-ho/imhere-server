package gdsc.binaryho.imhere.core.auth.exception;

import gdsc.binaryho.imhere.exception.ErrorInfo;
import gdsc.binaryho.imhere.exception.ImhereException;

public class MessagingServerException extends ImhereException {

    public static final ImhereException EXCEPTION = new MessagingServerException();

    private MessagingServerException() {
        super(ErrorInfo.MESSAGING_SERVER_EXCEPTION);
    }
}
