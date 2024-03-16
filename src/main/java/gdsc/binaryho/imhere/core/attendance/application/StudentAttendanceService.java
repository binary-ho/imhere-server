package gdsc.binaryho.imhere.core.attendance.application;


import gdsc.binaryho.imhere.core.attendance.domain.Attendance;
import gdsc.binaryho.imhere.core.attendance.application.port.AttendanceHistoryCacheRepository;
import gdsc.binaryho.imhere.core.attendance.domain.AttendanceHistory;
import gdsc.binaryho.imhere.core.attendance.exception.AttendanceNumberIncorrectException;
import gdsc.binaryho.imhere.core.attendance.exception.AttendanceTimeExceededException;
import gdsc.binaryho.imhere.core.attendance.infrastructure.AttendanceRepository;
import gdsc.binaryho.imhere.core.attendance.model.request.AttendanceRequest;
import gdsc.binaryho.imhere.core.attendance.model.response.StudentAttendanceResponse;
import gdsc.binaryho.imhere.core.attendance.model.response.StudentRecentAttendanceResponse;
import gdsc.binaryho.imhere.core.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.core.enrollment.EnrollmentState;
import gdsc.binaryho.imhere.core.enrollment.exception.EnrollmentNotApprovedException;
import gdsc.binaryho.imhere.core.enrollment.infrastructure.EnrollmentInfoRepository;
import gdsc.binaryho.imhere.core.lecture.LectureState;
import gdsc.binaryho.imhere.core.lecture.application.OpenLectureService;
import gdsc.binaryho.imhere.core.lecture.domain.Lecture;
import gdsc.binaryho.imhere.core.lecture.exception.LectureNotOpenException;
import gdsc.binaryho.imhere.core.member.Member;
import gdsc.binaryho.imhere.security.util.AuthenticationHelper;
import gdsc.binaryho.imhere.util.SeoulDateTimeHolder;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class StudentAttendanceService {

    private final OpenLectureService openLectureService;

    private final AttendanceRepository attendanceRepository;
    private final EnrollmentInfoRepository enrollmentRepository;
    private final AttendanceHistoryCacheRepository attendanceHistoryCacheRepository;

    private final ApplicationEventPublisher eventPublisher;

    private final SeoulDateTimeHolder seoulDateTimeHolder;
    private final AuthenticationHelper authenticationHelper;

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

    @Transactional
    public void takeAttendanceVer2(AttendanceRequest attendanceRequest, Long lectureId) {
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
    public StudentRecentAttendanceResponse getStudentRecentAttendance(Long lectureId) {
        Long studentId = authenticationHelper.getCurrentMember().getId();

        List<AttendanceHistory> attendanceHistories = attendanceHistoryCacheRepository
            .findAllByLectureIdAndStudentId(lectureId, studentId);

        if (attendanceHistories.isEmpty()) {
            List<String> timestamps = getRecentAttendanceTimestamps(lectureId, studentId);
            return new StudentRecentAttendanceResponse(timestamps);
        }

        List<String> timestamps = getTimestamps(attendanceHistories);
        return new StudentRecentAttendanceResponse(timestamps);
    }

    @Transactional(readOnly = true)
    public StudentAttendanceResponse getStudentDayAttendance(Long lectureId, Long milliseconds) {
        LocalDateTime timestamp = getTodaySeoulDateTime(milliseconds);
        Long studentId = authenticationHelper.getCurrentMember().getId();
        List<Attendance> attendances = attendanceRepository
            .findByLectureIdAndStudentIdAndTimestampBetween(
                lectureId, studentId, timestamp, timestamp.plusDays(1));

        return new StudentAttendanceResponse(attendances);
    }

    private List<String> getRecentAttendanceTimestamps(Long lectureId, Long studentId) {
        List<Attendance> attendances = findRecentAttendances(lectureId, studentId);
        List<String> timestamps = attendances.stream()
            .map(Attendance::getTimestamp)
            .map(LocalDateTime::toString)
            .collect(Collectors.toList());
        return timestamps;
    }

    private List<Attendance> findRecentAttendances(Long lectureId, Long studentId) {
        LocalDateTime now = seoulDateTimeHolder.getSeoulDateTime();
        LocalDateTime beforeRecentTime = now.minusHours(RECENT_TIME.toHours());

        List<Attendance> attendances = attendanceRepository
            .findByLectureIdAndStudentIdAndTimestampBetween(
                lectureId, studentId, beforeRecentTime, now);
        return attendances;
    }

    private List<String> getTimestamps(List<AttendanceHistory> attendanceHistories) {
        return attendanceHistories.stream()
            .map(AttendanceHistory::getTimestamp)
            .map(Objects::toString)
            .collect(Collectors.toList());
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

    private void attend(AttendanceRequest attendanceRequest, EnrollmentInfo enrollmentInfo) {
        Member student = enrollmentInfo.getMember();
        Lecture lecture = enrollmentInfo.getLecture();
        Attendance attendance = Attendance.createAttendance(
            student, lecture,
            attendanceRequest.getDistance(),
            attendanceRequest.getAccuracy(),
            seoulDateTimeHolder.from(attendanceRequest.getMilliseconds())
        );

        attendanceRepository.save(attendance);
        publishStudentAttendedEvent(attendance, lecture, student);
        logAttendanceHistory(enrollmentInfo, attendance);
    }

    private void publishStudentAttendedEvent(
        Attendance attendance, Lecture lecture, Member student) {
        LocalDateTime timestamp = attendance.getTimestamp();
        eventPublisher.publishEvent(
            new StudentAttendedEvent(lecture.getId(), student.getId(), timestamp));
    }

    private void logAttendanceHistory(EnrollmentInfo enrollmentInfo, Attendance attendance) {
        Lecture lecture = attendance.getLecture();
        Member attendMember = enrollmentInfo.getMember();
        log.info("[출석 완료] {}({}) , 학생 : {} ({})",
            lecture::getLectureName, lecture::getId,
            attendMember::getUnivId, attendMember::getName);
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
