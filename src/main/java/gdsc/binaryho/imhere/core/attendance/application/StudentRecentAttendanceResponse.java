package gdsc.binaryho.imhere.core.attendance.application;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.Getter;

@Getter
@Tag(name = "AttendanceNumberDto", description = "강사가 강의를 OPEN 할때 발급된 출석 번호")
public class StudentRecentAttendanceResponse {

    private final List<String> timestamps;

    private StudentRecentAttendanceResponse(List<String> timestamps) {
        this.timestamps = timestamps;
    }

    public static StudentRecentAttendanceResponse of(List<String> timestamps) {
        return new StudentRecentAttendanceResponse(timestamps);
    }
}
