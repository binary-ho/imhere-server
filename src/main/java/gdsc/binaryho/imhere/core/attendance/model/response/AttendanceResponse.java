package gdsc.binaryho.imhere.core.attendance.model.response;

import gdsc.binaryho.imhere.core.attendance.Attendance;
import gdsc.binaryho.imhere.core.lecture.Lecture;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
@Tag(name = "AttendanceDto", description = "한 강의의 출석 정보")
public class AttendanceResponse {

    private final String lectureName;
    private final String lecturerName;
    @Schema(description = "출석 정보 AttendanceInfo 리스트")
    private final List<AttendanceInfo> attendanceInfos;

    public AttendanceResponse(Lecture lecture, List<Attendance> attendances) {
        this.lectureName = lecture.getLectureName();
        this.lecturerName = lecture.getLecturerName();
        this.attendanceInfos = getAttendanceInfos(attendances);
    }

    private List<AttendanceInfo> getAttendanceInfos(List<Attendance> attendances) {
        return attendances.stream()
            .map(AttendanceInfo::new)
            .collect(Collectors.toList());
    }

    @Getter
    @Tag(name = "AttendanceInfo", description = "한 학생의 출석 정보")
    private static class AttendanceInfo {

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
    }
}
