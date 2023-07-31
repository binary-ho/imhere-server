package gdsc.binaryho.imhere.fixture;

import static gdsc.binaryho.imhere.fixture.LectureFixture.LECTURE;
import static gdsc.binaryho.imhere.fixture.MemberFixture.STUDENT;

import gdsc.binaryho.imhere.core.attendance.Attendance;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class AttendanceFixture {

    public static int ATTENDANCE_NUMBER = 777;
    public static String DISTANCE = "7777";
    public static String ACCURACY = "7";
    public static long MILLISECONDS = 831340800000L;
    public static LocalDateTime LOCAL_DATE_TIME = getLocalDateTime();

    public static Attendance ATTENDANCE = Attendance.createAttendance(
        STUDENT, LECTURE, DISTANCE, ACCURACY, LOCAL_DATE_TIME);

    private static LocalDateTime getLocalDateTime() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(MILLISECONDS), ZoneId.of("Asia/Seoul"));
    }
}
