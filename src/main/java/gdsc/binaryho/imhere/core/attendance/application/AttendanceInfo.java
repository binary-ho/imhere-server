package gdsc.binaryho.imhere.core.attendance.application;

import gdsc.binaryho.imhere.core.attendance.Attendance;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
@Tag(name = "AttendanceInfo", description = "한 학생의 출석 정보")
public class AttendanceInfo {

    private final String univId;
    private final String name;
    private final String distance;
    private final String accuracy;
    private final LocalDateTime timestamp;

    private AttendanceInfo(Attendance attendance) {
        this.univId = attendance.getMember().getUnivId();
        this.name = attendance.getMember().getName();
        this.distance = attendance.getDistance();
        this.accuracy = attendance.getAccuracy();
        this.timestamp = attendance.getTimestamp();
    }

    public static List<AttendanceInfo> getAttendanceInfos(List<Attendance> attendances) {
        return attendances.stream().map(AttendanceInfo::new)
            .collect(Collectors.toList());
    }
}
