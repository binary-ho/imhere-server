package gdsc.binaryho.imhere.fixture;

import static gdsc.binaryho.imhere.fixture.LectureFixture.OPEN_LECTURE;

import gdsc.binaryho.imhere.core.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.core.enrollment.EnrollmentState;

public class EnrollmentInfoFixture {

    public static final EnrollmentInfo ENROLLMENT_INFO = EnrollmentInfo
        .createEnrollmentInfo(OPEN_LECTURE, MemberFixture.STUDENT, EnrollmentState.APPROVAL);
}
