package gdsc.binaryho.imhere.core.attendance.model.response;

import gdsc.binaryho.imhere.core.attendance.domain.Attendance;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
@Tag(name = "LecturerAttendanceResponse", description = "강사를 위한 강의 출석 기록")
public class LecturerAttendanceResponse {

    @Schema(description = "출석 정보 AttendanceInfo 리스트")
    private final List<AttendanceHistory> attendances;

    public LecturerAttendanceResponse(List<Attendance> attendances) {
        this.attendances = attendances.stream()
            .map(AttendanceHistory::new)
            .collect(Collectors.toList());
    }

    @Getter
    @Tag(name = "AttendanceHistory", description = "학생 출석 기록")
    public static class AttendanceHistory {

        private final String univId;
        private final String name;
        private final String distance;
        private final String accuracy;
        private final LocalDateTime timestamp;

        private AttendanceHistory(Attendance attendance) {
            this.univId = attendance.getStudent().getUnivId();
            this.name = attendance.getStudent().getName();
            this.distance = attendance.getDistance();
            this.accuracy = attendance.getAccuracy();
            this.timestamp = attendance.getTimestamp();
        }
    }
}
