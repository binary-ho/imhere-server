package gdsc.binaryho.imhere.fixture;

import static gdsc.binaryho.imhere.fixture.MemberFixture.MOCK_LECTURER;

import gdsc.binaryho.imhere.core.lecture.LectureState;
import gdsc.binaryho.imhere.core.lecture.domain.Lecture;

public class LectureFixture {

    public static final Lecture MOCK_LECTURE = getMockLecture();
    public static final Lecture MOCK_OPEN_LECTURE = getMockOpenLecture();
    public static final Lecture MOCK_CLOSED_LECTURE = getMockClosedLecture();

    private static Lecture getMockLecture() {
        Lecture lecture = Lecture.createLecture(MOCK_LECTURER, "mockLecture");
        lecture.setId(1L);
        return lecture;
    }

    private static Lecture getMockOpenLecture() {
        Lecture lecture = Lecture.createLecture(MOCK_LECTURER, "MockOpenLecture");
        lecture.setId(2L);
        lecture.setLectureState(LectureState.OPEN);
        return lecture;
    }

    private static Lecture getMockClosedLecture() {
        Lecture lecture = Lecture.createLecture(MOCK_LECTURER, "MockClosedLecture");
        lecture.setId(3L);
        lecture.setLectureState(LectureState.CLOSED);
        return lecture;
    }
}
