package gdsc.binaryho.imhere.core.attendance.application;


import static gdsc.binaryho.imhere.core.attendance.application.AttendanceSaveRequestStatus.NO_REQUEST;
import static gdsc.binaryho.imhere.core.attendance.application.AttendanceSaveRequestStatus.SUCCESS;

import gdsc.binaryho.imhere.core.attendance.application.port.AttendanceHistoryCacheRepository;
import gdsc.binaryho.imhere.core.attendance.domain.Attendance;
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
import gdsc.binaryho.imhere.core.lecture.exception.LectureNotFoundException;
import gdsc.binaryho.imhere.core.lecture.exception.LectureNotOpenException;
import gdsc.binaryho.imhere.core.lecture.infrastructure.LectureRepository;
import gdsc.binaryho.imhere.core.member.Member;
import gdsc.binaryho.imhere.security.util.AuthenticationHelper;
import gdsc.binaryho.imhere.util.SeoulDateTimeHolder;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentAttendanceService {

    private final OpenLectureService openLectureService;
    private final AttendanceSaveService attendanceSaveService;

    private final LectureRepository lectureRepository;
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

        if (isOpenLectureCacheNotExist(currentStudent.getId(), lectureId)) {
            attendWithValidateEnrollment(attendanceRequest, currentStudent, lectureId);
            return;
        }

        Lecture lecture = lectureRepository.findById(lectureId)
            .orElseThrow(() -> LectureNotFoundException.EXCEPTION);
        attend(attendanceRequest, currentStudent, lecture);
    }

    @Transactional(readOnly = true)
    public StudentRecentAttendanceResponse getStudentRecentAttendanceStatus(Long lectureId) {
        Long studentId = authenticationHelper.getCurrentMember().getId();
        AttendanceSaveRequestStatus attendanceSaveRequestStatus = attendanceHistoryCacheRepository
            .getRequestStatusByLectureIdAndStudentId(lectureId, studentId);

        if (isRequestExist(attendanceSaveRequestStatus)) {
            return new StudentRecentAttendanceResponse(attendanceSaveRequestStatus);
        }

        return getStudentRecentAttendanceStatus(lectureId, studentId);
    }

    private boolean isRequestExist(AttendanceSaveRequestStatus attendanceSaveRequestStatus) {
        return !attendanceSaveRequestStatus.equals(NO_REQUEST);
    }

    private StudentRecentAttendanceResponse getStudentRecentAttendanceStatus(Long lectureId, Long studentId) {
        if (isRecentAttendancesExist(lectureId, studentId)) {
            return new StudentRecentAttendanceResponse(SUCCESS);
        }
        return new StudentRecentAttendanceResponse(NO_REQUEST);
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

    private boolean isOpenLectureCacheNotExist(Long studentId, Long lectureId) {
        return !openLectureService.isStudentOpenLectureExist(studentId, lectureId);
    }

    private Boolean isRecentAttendancesExist(Long lectureId, Long studentId) {
        LocalDateTime now = seoulDateTimeHolder.getSeoulDateTime();
        LocalDateTime beforeRecentTime = now.minusHours(RECENT_TIME.toHours());

        List<Attendance> attendances = attendanceRepository
            .findByLectureIdAndStudentIdAndTimestampBetween(
                lectureId, studentId, beforeRecentTime, now);
        return !attendances.isEmpty();
    }

    private void validateLectureOpen(EnrollmentInfo enrollmentInfo) {
        if (enrollmentInfo.getLecture().getLectureState() != LectureState.OPEN) {
            throw LectureNotOpenException.EXCEPTION;
        }
    }

    private void validateAttendanceNumber(Long lectureId, int attendanceNumber) {
        Integer actualAttendanceNumber = openLectureService.findAttendanceNumber(lectureId);

        validateAttendanceNumberNotTimeOut(actualAttendanceNumber);
        validateAttendanceNumberCorrect(actualAttendanceNumber, attendanceNumber);
    }

    private void attendWithValidateEnrollment(
        AttendanceRequest attendanceRequest, Member student, Long lectureId) {
        EnrollmentInfo enrollmentInfo = findApprovalEnrollment(lectureId, student);
        validateLectureOpen(enrollmentInfo);
        attend(attendanceRequest, enrollmentInfo.getMember(), enrollmentInfo.getLecture());
    }

    private EnrollmentInfo findApprovalEnrollment(Long lectureId, Member student) {
        return enrollmentRepository
            .findByMemberIdAndLectureIdAndEnrollmentState(
                student.getId(), lectureId, EnrollmentState.APPROVAL)
            .orElseThrow(() -> EnrollmentNotApprovedException.EXCEPTION);
    }

    private void attend(AttendanceRequest attendanceRequest, Member student, Lecture lecture) {
        validateAttendanceNumber(lecture.getId(), attendanceRequest.getAttendanceNumber());

        Attendance attendance = Attendance.createAttendance(
            student, lecture,
            attendanceRequest.getDistance(),
            attendanceRequest.getAccuracy(),
            seoulDateTimeHolder.from(attendanceRequest.getMilliseconds())
        );

        saveAttendanceAsynchronously(attendance);
        publishAttendanceRequestedEvent(attendance);
    }

    private void saveAttendanceAsynchronously(Attendance attendance) {
        CompletableFuture.runAsync(
            () -> attendanceSaveService.save(attendance)
        ).thenRun(
            () -> publishAttendanceSaveSucceedEvent(attendance)
        ).exceptionally(
            exception -> publishAttendanceFailedEvent(attendance, exception)
        );
    }

    private void publishAttendanceSaveSucceedEvent(Attendance attendance) {
        Long lectureId = attendance.getLecture().getId();
        Long studentId = attendance.getStudent().getId();
        AttendanceSaveSucceedEvent event = new AttendanceSaveSucceedEvent(lectureId, studentId);
        eventPublisher.publishEvent(event);
    }

    private Void publishAttendanceFailedEvent(Attendance attendance, Throwable throwable) {
        Long lectureId = attendance.getLecture().getId();
        Long studentId = attendance.getStudent().getId();
        AttendanceFailedEvent event = new AttendanceFailedEvent(lectureId, studentId, throwable);
        eventPublisher.publishEvent(event);
        return null;
    }

    private void publishAttendanceRequestedEvent(Attendance attendance) {
        Long lectureId = attendance.getLecture().getId();
        Long studentId = attendance.getStudent().getId();
        eventPublisher.publishEvent(
            new AttendanceRequestedEvent(lectureId, studentId));
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
