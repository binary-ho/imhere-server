package gdsc.binaryho.imhere.domain.lecture;

import lombok.Getter;

@Getter
public class LectureStateChangeRequest {

    private final Long lecturerId;
    private final LectureState lectureState;

    public LectureStateChangeRequest(Long lecturerId, LectureState lectureState) {
        this.lecturerId = lecturerId;
        this.lectureState = lectureState;
    }
}
