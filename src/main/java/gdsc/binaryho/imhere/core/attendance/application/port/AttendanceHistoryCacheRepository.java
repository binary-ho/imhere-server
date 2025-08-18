package gdsc.binaryho.imhere.core.attendance.application.port;

import gdsc.binaryho.imhere.core.attendance.application.AttendanceSaveRequestStatus;
import gdsc.binaryho.imhere.core.attendance.domain.AttendanceHistory;

public interface AttendanceHistoryCacheRepository {

    void cache(AttendanceHistory attendanceHistory);

    AttendanceSaveRequestStatus getRequestStatusByLectureIdAndStudentId(Long lectureId, Long studentId);
}
