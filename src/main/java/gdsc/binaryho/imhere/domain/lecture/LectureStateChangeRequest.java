package gdsc.binaryho.imhere.domain.lecture;

import lombok.Getter;

@Getter
public class LectureStateChangeRequest {

    private final LectureState lectureState;

    public LectureStateChangeRequest(LectureState lectureState) {
        this.lectureState = lectureState;
    }
}
