package gdsc.binaryho.imhere.core.lecture.exception;

import gdsc.binaryho.imhere.exception.ErrorInfo;
import gdsc.binaryho.imhere.exception.ImhereException;

public class UnexpectedRedisDataTypeException extends ImhereException {

    public static final ImhereException EXCEPTION = new UnexpectedRedisDataTypeException();

    private UnexpectedRedisDataTypeException() {
        super(ErrorInfo.UNEXPECTED_REDIS_DATA_TYPE);
    }
}
