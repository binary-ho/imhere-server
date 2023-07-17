package gdsc.binaryho.imhere.core.attendance.model.response;

import gdsc.binaryho.imhere.core.lecture.Lecture;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.Getter;

@Getter
@Tag(name = "AttendanceDto", description = "한 강의의 출석 정보")
public class AttendanceResponse {

    private final String lectureName;
    private final String lecturerName;
    @Schema(description = "출석 정보 AttendanceInfo 리스트")
    private final List<AttendanceInfo> attendanceInfoRespons;

    public AttendanceResponse(Lecture lecture, List<AttendanceInfo> attendanceInfoRespons) {
        this.lectureName = lecture.getLectureName();
        this.lecturerName = lecture.getLecturerName();
        this.attendanceInfoRespons = attendanceInfoRespons;
    }
}
