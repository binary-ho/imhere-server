package gdsc.binaryho.imhere.mock.fixture;

import gdsc.binaryho.imhere.core.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.core.enrollment.EnrollmentState;

public class EnrollmentInfoFixture {

    public static final EnrollmentInfo MOCK_ENROLLMENT_INFO = EnrollmentInfo
        .createEnrollmentInfo(LectureFixture.MOCK_OPEN_LECTURE, MemberFixture.MOCK_STUDENT, EnrollmentState.APPROVAL);
}
