package gdsc.binaryho.imhere.core.attendance.application;


import gdsc.binaryho.imhere.core.attendance.Attendance;
import gdsc.binaryho.imhere.core.attendance.exception.AttendanceNumberIncorrectException;
import gdsc.binaryho.imhere.core.attendance.exception.AttendanceTimeExceededException;
import gdsc.binaryho.imhere.core.attendance.infrastructure.AttendanceRepository;
import gdsc.binaryho.imhere.core.attendance.model.request.AttendanceRequest;
import gdsc.binaryho.imhere.core.attendance.model.response.LecturerAttendanceResponse;
import gdsc.binaryho.imhere.core.attendance.model.response.StudentAttendanceResponse;
import gdsc.binaryho.imhere.core.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.core.enrollment.EnrollmentState;
import gdsc.binaryho.imhere.core.enrollment.exception.EnrollmentNotApprovedException;
import gdsc.binaryho.imhere.core.enrollment.infrastructure.EnrollmentInfoRepository;
import gdsc.binaryho.imhere.core.lecture.LectureState;
import gdsc.binaryho.imhere.core.lecture.application.OpenLectureService;
import gdsc.binaryho.imhere.core.lecture.domain.Lecture;
import gdsc.binaryho.imhere.core.lecture.exception.LectureNotOpenException;
import gdsc.binaryho.imhere.core.member.Member;
import gdsc.binaryho.imhere.core.member.Role;
import gdsc.binaryho.imhere.security.util.AuthenticationHelper;
import gdsc.binaryho.imhere.util.SeoulDateTimeHolder;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AuthenticationHelper authenticationHelper;
    private final OpenLectureService openLectureService;
    private final AttendanceRepository attendanceRepository;
    private final EnrollmentInfoRepository enrollmentRepository;
    private final SeoulDateTimeHolder seoulDateTimeHolder;

    private final static Duration RECENT_TIME = Duration.ofHours(1L);

    @Transactional
    public void takeAttendance(AttendanceRequest attendanceRequest, Long lectureId) {
        Member currentStudent = authenticationHelper.getCurrentMember();
        EnrollmentInfo enrollmentInfo = enrollmentRepository
            .findByMemberIdAndLectureIdAndEnrollmentState(currentStudent.getId(), lectureId,
                EnrollmentState.APPROVAL)
            .orElseThrow(() -> EnrollmentNotApprovedException.EXCEPTION);

        validateLectureOpen(enrollmentInfo);
        validateAttendanceNumber(enrollmentInfo, attendanceRequest.getAttendanceNumber());

        attend(attendanceRequest, enrollmentInfo);
    }

    @Transactional(readOnly = true)
    public StudentAttendanceResponse getStudentRecentAttendance(Long lectureId) {
        Long studentId = authenticationHelper.getCurrentMember().getId();
        LocalDateTime now = seoulDateTimeHolder.getSeoulDateTime();
        LocalDateTime beforeRecentTime = now.minusHours(RECENT_TIME.toHours());

        List<Attendance> attendances = attendanceRepository
            .findByLectureIdAndStudentIdAndTimestampBetween(lectureId, studentId, beforeRecentTime, now);
        return new StudentAttendanceResponse(attendances);
    }

    @Transactional(readOnly = true)
    public StudentAttendanceResponse getStudentDayAttendance(Long lectureId, Long milliseconds) {
        LocalDateTime timestamp = getTodaySeoulDateTime(milliseconds);
        Long studentId = authenticationHelper.getCurrentMember().getId();
        List<Attendance> attendances = attendanceRepository
            .findByLectureIdAndStudentIdAndTimestampBetween(lectureId, studentId,
                timestamp, timestamp.plusDays(1));

        return new StudentAttendanceResponse(attendances);
    }

    @Transactional(readOnly = true)
    public LecturerAttendanceResponse getLecturerAttendances(Long lectureId) {
        authenticationHelper.verifyMemberHasRole(Role.LECTURER);

        List<Attendance> attendances = attendanceRepository.findAllByLectureId(lectureId);
        return new LecturerAttendanceResponse(attendances);
    }

    @Transactional(readOnly = true)
    public LecturerAttendanceResponse getLecturerDayAttendances(Long lectureId, Long milliseconds) {
        authenticationHelper.verifyMemberHasRole(Role.LECTURER);

        LocalDateTime timestamp = getTodaySeoulDateTime(milliseconds);
        List<Attendance> attendances = attendanceRepository
            .findByLectureIdAndTimestampBetween(lectureId, timestamp, timestamp.plusDays(1));
        return new LecturerAttendanceResponse(attendances);
    }

    private void attend(AttendanceRequest attendanceRequest, EnrollmentInfo enrollmentInfo) {
        Attendance attendance = Attendance.createAttendance(
            enrollmentInfo.getMember(),
            enrollmentInfo.getLecture(),
            attendanceRequest.getDistance(),
            attendanceRequest.getAccuracy(),
            seoulDateTimeHolder.from(attendanceRequest.getMilliseconds())
        );

        attendanceRepository.save(attendance);
        logAttendanceHistory(enrollmentInfo, attendance);
    }

    private void logAttendanceHistory(EnrollmentInfo enrollmentInfo, Attendance attendance) {
        Lecture lecture = attendance.getLecture();
        Member attendMember = enrollmentInfo.getMember();
        log.info("[출석 완료] {}({}) , 학생 : {} ({})",
            lecture::getLectureName, lecture::getId,
            attendMember::getUnivId, attendMember::getName);
    }

    private void validateLectureOpen(EnrollmentInfo enrollmentInfo) {
        if (enrollmentInfo.getLecture().getLectureState() != LectureState.OPEN) {
            throw LectureNotOpenException.EXCEPTION;
        }
    }

    private void validateAttendanceNumber(EnrollmentInfo enrollmentInfo, int attendanceNumber) {
        long lectureId = enrollmentInfo.getLecture().getId();
        Integer actualAttendanceNumber = openLectureService.findAttendanceNumber(lectureId);

        validateAttendanceNumberNotTimeOut(actualAttendanceNumber);
        validateAttendanceNumberCorrect(actualAttendanceNumber, attendanceNumber);
    }

    private void validateAttendanceNumberNotTimeOut(Integer attendanceNumber) {
        if (attendanceNumber == null) {
            throw AttendanceTimeExceededException.EXCEPTION;
        }
    }

    private void validateAttendanceNumberCorrect(Integer actualAttendanceNumber,
        int attendanceNumber) {
        if (actualAttendanceNumber != attendanceNumber) {
            throw AttendanceNumberIncorrectException.EXCEPTION;
        }
    }

    private LocalDateTime getTodaySeoulDateTime(Long milliseconds) {
        return seoulDateTimeHolder.from(milliseconds)
            .withHour(0).withMinute(0).withSecond(0);
    }
}
