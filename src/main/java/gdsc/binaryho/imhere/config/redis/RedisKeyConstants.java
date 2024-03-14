package gdsc.binaryho.imhere.config.redis;

public class RedisKeyConstants {

    public static final String ATTENDANCE_NUMBER_KEY_PREFIX = "$lecture_id$";
    public static final String VERIFICATION_CODE_KEY_PREFIX = "$email$";
    public static final String OPEN_LECTURE_KEY_PREFIX = "$open_lecture$";
    public static final String LECTURE_STUDENT_KEY_PREFIX = "$lecture_student$";
    public static final String ATTENDANCE_HISTORY_KEY_FORMAT = "$attendance_history$lecture_id:%d$student_id:%d$";
}
