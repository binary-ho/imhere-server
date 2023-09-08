package gdsc.binaryho.imhere.mock.fixture;

import gdsc.binaryho.imhere.core.attendance.Attendance;
import gdsc.binaryho.imhere.mock.FixedSeoulTimeHolder;
import java.time.LocalDateTime;

public class AttendanceFixture {

    public static int ATTENDANCE_NUMBER = 777;
    public static String DISTANCE = "7777";
    public static String ACCURACY = "7";
    public static long MILLISECONDS = 831340800000L;
    public static LocalDateTime LOCAL_DATE_TIME = getLocalDateTime();

    public static Attendance MOCK_ATTENDANCE = Attendance.createAttendance(
        MemberFixture.MOCK_STUDENT, LectureFixture.MOCK_LECTURE, DISTANCE, ACCURACY, LOCAL_DATE_TIME);

    private static LocalDateTime getLocalDateTime() {
        return new FixedSeoulTimeHolder().from(MILLISECONDS);
    }
}
