package gdsc.binaryho.imhere.mapper.dtos;

import gdsc.binaryho.imhere.domain.lecture.Lecture;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.Getter;

@Getter
@Tag(name = "AttendanceDto", description = "한 강의의 출석 정보")
public class AttendanceDto {

    private final String lectureName;
    private final String lecturerName;
    @Schema(description = "출석 정보 AttendanceInfo 리스트")
    private final List<AttendanceInfo> attendanceInfos;

    public AttendanceDto(Lecture lecture, List<AttendanceInfo> attendanceInfos) {
        this.lectureName = lecture.getLectureName();
        this.lecturerName = lecture.getLecturerName();
        this.attendanceInfos = attendanceInfos;
    }
}
