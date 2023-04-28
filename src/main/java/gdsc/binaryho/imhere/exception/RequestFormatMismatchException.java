package gdsc.binaryho.imhere.exception;

public class RequestFormatMismatchException extends ImhereException {

    public static final ImhereException EXCEPTION = new RequestFormatMismatchException();

    private RequestFormatMismatchException() {
        super(ErrorCode.REQUEST_FORMAT_MISMATCH);
    }
}
