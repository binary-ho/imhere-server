package gdsc.binaryho.imhere.core.lecture.exception;

import gdsc.binaryho.imhere.exception.ErrorInfo;
import gdsc.binaryho.imhere.exception.ImhereException;

public class LectureNotOpenException extends ImhereException {

    public static final ImhereException EXCEPTION = new LectureNotOpenException();

    private LectureNotOpenException() {
        super(ErrorInfo.LECTURE_NOT_OPEN);
    }
}
