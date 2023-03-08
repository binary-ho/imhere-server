package gdsc.binaryho.imhere.service;


import gdsc.binaryho.imhere.domain.attendance.Attendance;
import gdsc.binaryho.imhere.domain.attendance.AttendanceRequest;
import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfoRepository;
import gdsc.binaryho.imhere.domain.lecture.LectureState;
import java.rmi.NoSuchObjectException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AttendanceService {

    private final EnrollmentInfoRepository enrollmentRepository;

    public AttendanceService(
        EnrollmentInfoRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    @Transactional
    public void takeAttendance(AttendanceRequest attendanceRequest,
        Long studentId, Long lectureId) throws NoSuchObjectException {
        EnrollmentInfo enrollmentInfo = enrollmentRepository
            .findByMemberIdAndLectureId(studentId, lectureId)
            .orElseThrow();

        if (enrollmentInfo.getLecture().getLectureState() != LectureState.OPEN) {
            /* TODO: 예외 대체 필요 */
            throw new NoSuchObjectException("lecture is not opened");
        }

        LocalDateTime localDateTime = getLocalDateTime(attendanceRequest.getMilliseconds());

        Attendance.createAttendance(enrollmentInfo,
            attendanceRequest.getDistance(), attendanceRequest.getAccuracy(), localDateTime);
    }

    private LocalDateTime getLocalDateTime(Long milliseconds) {
        return LocalDateTime
            .ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.of("Asia/Seoul"));
    }
}
