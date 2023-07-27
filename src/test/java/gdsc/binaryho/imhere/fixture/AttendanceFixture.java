package gdsc.binaryho.imhere.fixture;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class AttendanceFixture {

    public static int ATTENDANCE_NUMBER = 777;
    public static String DISTANCE = "7777";
    public static String ACCURACY = "7";
    public static long MILLISECONDS = 831340800000L;
    public static LocalDateTime LOCAL_DATE_TIME = getLocalDateTime();

    private static LocalDateTime getLocalDateTime() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(MILLISECONDS), ZoneId.of("Asia/Seoul"));
    }
}
