package gdsc.binaryho.imhere.core.attendance.model.response;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;

@Getter
@Tag(name = "AttendanceNumberDto", description = "강사가 강의를 OPEN 할때 발급된 출석 번호")
public class AttendanceNumberResponse {

    private int attendanceNumber;

    public AttendanceNumberResponse(int attendanceNumber) {
        this.attendanceNumber = attendanceNumber;
    }
}
