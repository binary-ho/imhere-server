package gdsc.binaryho.imhere.core.attendance.model.response;

import gdsc.binaryho.imhere.core.attendance.application.AttendanceSaveRequestStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;

@Getter
@Tag(name = "StudentRecentAttendanceResponse", description = "학생의 최근 출석 상태")
public class StudentRecentAttendanceResponse {

    private final String recentAttendanceStatus;

    public StudentRecentAttendanceResponse(AttendanceSaveRequestStatus status) {
        this.recentAttendanceStatus = status.name();
    }
}
