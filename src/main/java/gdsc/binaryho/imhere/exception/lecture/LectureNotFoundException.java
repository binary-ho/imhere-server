package gdsc.binaryho.imhere.exception.lecture;

import gdsc.binaryho.imhere.exception.ErrorCode;
import gdsc.binaryho.imhere.exception.ImhereException;

public class LectureNotFoundException extends ImhereException {

    public static final ImhereException EXCEPTION = new LectureNotFoundException();

    private LectureNotFoundException() {
        super(ErrorCode.LECTURE_NOT_FOUND);
    }
}
