package gdsc.binaryho.imhere.fixture;

import static gdsc.binaryho.imhere.fixture.MemberFixture.LECTURER;

import gdsc.binaryho.imhere.core.lecture.LectureState;
import gdsc.binaryho.imhere.core.lecture.domain.Lecture;

public class LectureFixture {

    public static final Lecture LECTURE = getMockLecture();
    public static final Lecture OPEN_STATE_LECTURE = getMockOpenLecture();
    public static final Lecture CLOSED_STATE_LECTURE = getMockClosedLecture();

    private static Lecture getMockLecture() {
        Lecture lecture = Lecture.createLecture(LECTURER, "mockLecture");
        lecture.setId(1L);
        return lecture;
    }

    private static Lecture getMockOpenLecture() {
        Lecture lecture = Lecture.createLecture(LECTURER, "mockLecture");
        lecture.setId(2L);
        lecture.setLectureState(LectureState.OPEN);
        return lecture;
    }

    private static Lecture getMockClosedLecture() {
        Lecture lecture = Lecture.createLecture(LECTURER, "mockLecture");
        lecture.setId(3L);
        lecture.setLectureState(LectureState.CLOSED);
        return lecture;
    }
}
