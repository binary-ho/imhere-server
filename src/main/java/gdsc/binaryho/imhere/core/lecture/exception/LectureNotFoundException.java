package gdsc.binaryho.imhere.core.lecture.exception;

import gdsc.binaryho.imhere.exception.ErrorInfo;
import gdsc.binaryho.imhere.exception.ImhereException;

public class LectureNotFoundException extends ImhereException {

    public static final ImhereException EXCEPTION = new LectureNotFoundException();

    private LectureNotFoundException() {
        super(ErrorInfo.LECTURE_NOT_FOUND);
    }
}
