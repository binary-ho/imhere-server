package gdsc.binaryho.imhere.core.attendance.application.port;

public interface AttendanceNumberRepository {

    String getByLectureId(Long lectureId);

    void saveByLectureId(Long lectureId, int attendanceNumber);
}
