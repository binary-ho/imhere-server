package gdsc.binaryho.imhere.core.attendance.application.port;

import gdsc.binaryho.imhere.core.attendance.domain.AttendanceHistories;
import gdsc.binaryho.imhere.core.attendance.domain.AttendanceHistory;

public interface AttendanceHistoryCacheRepository {

    AttendanceHistories findAllByLectureIdAndStudentId(long lectureId, long studentId);

    void cache(AttendanceHistory attendanceHistory);
}
