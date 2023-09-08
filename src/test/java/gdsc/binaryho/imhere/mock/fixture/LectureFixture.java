package gdsc.binaryho.imhere.mock.fixture;

import gdsc.binaryho.imhere.core.lecture.LectureState;
import gdsc.binaryho.imhere.core.lecture.domain.Lecture;
import gdsc.binaryho.imhere.mock.FixedSeoulTimeHolder;

public class LectureFixture {

    public static final Lecture MOCK_LECTURE = getMockLecture();
    public static final Lecture MOCK_OPEN_LECTURE = getMockOpenLecture();
    public static final Lecture MOCK_CLOSED_LECTURE = getMockClosedLecture();

    private static Lecture getMockLecture() {
        Lecture lecture = Lecture.createLecture(
            MemberFixture.MOCK_LECTURER, "mockLecture", FixedSeoulTimeHolder.FIXED_LOCAL_DATE_TIME);
        lecture.setId(1L);
        return lecture;
    }

    private static Lecture getMockOpenLecture() {
        Lecture lecture = Lecture.createLecture(
            MemberFixture.MOCK_LECTURER, "MockOpenLecture", FixedSeoulTimeHolder.FIXED_LOCAL_DATE_TIME);
        lecture.setId(2L);
        lecture.setLectureState(LectureState.OPEN);
        return lecture;
    }

    private static Lecture getMockClosedLecture() {
        Lecture lecture = Lecture.createLecture(
            MemberFixture.MOCK_LECTURER, "MockClosedLecture", FixedSeoulTimeHolder.FIXED_LOCAL_DATE_TIME);
        lecture.setId(3L);
        lecture.setLectureState(LectureState.CLOSED);
        return lecture;
    }
}
