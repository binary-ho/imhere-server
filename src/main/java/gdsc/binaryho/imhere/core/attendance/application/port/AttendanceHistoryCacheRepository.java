package gdsc.binaryho.imhere.core.attendance.application.port;

import gdsc.binaryho.imhere.core.attendance.domain.AttendanceHistory;
import java.util.List;

public interface AttendanceHistoryCacheRepository {

    List<AttendanceHistory> findAllByLectureIdAndStudentId(long lectureId, long studentId);

    void cache(AttendanceHistory attendanceHistory);
}
