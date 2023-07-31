package gdsc.binaryho.imhere.fixture;

import static gdsc.binaryho.imhere.fixture.MemberFixture.LECTURER;

import gdsc.binaryho.imhere.core.lecture.Lecture;
import gdsc.binaryho.imhere.core.lecture.LectureState;

public class LectureFixture {

    public static final Lecture LECTURE = getMockLecture();
    public static final Lecture OPEN_LECTURE = getMockOpenLecture();

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
}
