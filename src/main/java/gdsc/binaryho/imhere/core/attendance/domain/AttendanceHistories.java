package gdsc.binaryho.imhere.core.attendance.domain;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AttendanceHistories {

    private final List<AttendanceHistory> histories;

    public static AttendanceHistories of(List<AttendanceHistory> attendanceHistories) {
        return new AttendanceHistories(attendanceHistories);
    }

    public boolean isNotEmpty() {
        return !histories.isEmpty();
    }
}
