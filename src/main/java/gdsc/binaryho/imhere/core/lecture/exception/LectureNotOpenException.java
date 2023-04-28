package gdsc.binaryho.imhere.core.lecture.exception;

import gdsc.binaryho.imhere.exception.ErrorCode;
import gdsc.binaryho.imhere.exception.ImhereException;

public class LectureNotOpenException extends ImhereException {

    public static final ImhereException EXCEPTION = new LectureNotOpenException();

    private LectureNotOpenException() {
        super(ErrorCode.LECTURE_NOT_OPEN);
    }
}