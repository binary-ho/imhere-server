package gdsc.binaryho.imhere.mapper.dtos;

import gdsc.binaryho.imhere.domain.lecture.Lecture;
import java.util.List;

public class AttendanceDto {

    private final String lectureName;
    private final String lecturerName;
    private final List<AttendanceInfo> attendanceInfos;

    public AttendanceDto(Lecture lecture, List<AttendanceInfo> attendanceInfos) {
        this.lectureName = lecture.getLectureName();
        this.lecturerName = lecture.getLecturerName();
        this.attendanceInfos = attendanceInfos;
    }
}
