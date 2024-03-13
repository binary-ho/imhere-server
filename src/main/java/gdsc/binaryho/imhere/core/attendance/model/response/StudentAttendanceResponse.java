package gdsc.binaryho.imhere.core.attendance.model.response;

import gdsc.binaryho.imhere.core.attendance.Attendance;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
@Tag(name = "StudentAttendanceResponse", description = "학생의 강의 출석 기록")
public class StudentAttendanceResponse {

    @Schema(description = "출석 정보 AttendanceInfo 리스트")
    private final List<AttendanceHistory> attendances;

    public StudentAttendanceResponse(List<Attendance> attendances) {
        this.attendances = attendances.stream()
            .map(AttendanceHistory::new)
            .collect(Collectors.toList());
    }

    @Getter
    @Tag(name = "AttendanceHistory", description = "출석 기록들")
    public static class AttendanceHistory {

        private final String distance;
        private final String accuracy;
        private final LocalDateTime timestamp;

        private AttendanceHistory(Attendance attendance) {
            this.distance = attendance.getDistance();
            this.accuracy = attendance.getAccuracy();
            this.timestamp = attendance.getTimestamp();
        }
    }
}
