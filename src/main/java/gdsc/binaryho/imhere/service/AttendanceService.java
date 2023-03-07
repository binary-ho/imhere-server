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

public class AttendanceService {

    private final EnrollmentInfoRepository enrollmentRepository;

    public AttendanceService(
        EnrollmentInfoRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    @Transactional
    public void takeAttendance(AttendanceRequest attendanceRequest) throws NoSuchObjectException {
        EnrollmentInfo enrollmentInfo = enrollmentRepository
            .findByMemberIdAndLectureId(attendanceRequest.getMember_id(), attendanceRequest.getLecture_id())
            .orElseThrow();

        if (enrollmentInfo.getLecture().getLectureState() != LectureState.OPEN) {
            /* 예외 대체 필요 */
            throw new NoSuchObjectException("lecture is not opened");
        }

        LocalDateTime localDateTime = getLocalDateTime(attendanceRequest.getMilliseconds());

        Attendance.createAttendance(enrollmentInfo,
            attendanceRequest.getDistance(), attendanceRequest.getAccuracy(), localDateTime);
    }

    private LocalDateTime getLocalDateTime(Long milliseconds ) {
        return LocalDateTime
            .ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.of("Asia/Seoul"));
    }
}

/*
*    private Long lecture_id;
    private Long member_id;
    private String distance;
    private String accuracy;
    private Long timestamp;
*
*
* */
