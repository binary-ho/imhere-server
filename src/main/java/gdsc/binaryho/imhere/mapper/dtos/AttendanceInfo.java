package gdsc.binaryho.imhere.mapper.dtos;

import gdsc.binaryho.imhere.domain.attendance.Attendance;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class AttendanceInfo {

    private final String studentUnivId;
    private final String studentName;
    private final String distance;
    private final String accuracy;
    private final LocalDateTime timestamp;

    private AttendanceInfo(Attendance attendance) {
        this.studentUnivId = attendance.getMember().getUnivId();
        this.studentName = attendance.getMember().getName();
        this.distance = attendance.getDistance();
        this.accuracy = attendance.getAccuracy();
        this.timestamp = attendance.getTimestamp();
    }

    public static List<AttendanceInfo> getAttendanceInfos(List<Attendance> attendances) {
        return attendances.stream().map(AttendanceInfo::new)
            .collect(Collectors.toList());
    }
}
