package gdsc.binaryho.imhere.core.attendance.application.port;

public interface AttendanceNumberRepository {

    Integer getByLectureId(Long lectureId);

    void saveWithLectureIdAsKey(Long lectureId, int attendanceNumber);
}
