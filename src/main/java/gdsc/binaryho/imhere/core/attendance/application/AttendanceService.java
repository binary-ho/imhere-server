package gdsc.binaryho.imhere.core.attendance.application;


import gdsc.binaryho.imhere.core.attendance.Attendance;
import gdsc.binaryho.imhere.core.attendance.exception.AttendanceNumberIncorrectException;
import gdsc.binaryho.imhere.core.attendance.exception.AttendanceTimeExceededException;
import gdsc.binaryho.imhere.core.attendance.infrastructure.AttendanceRepository;
import gdsc.binaryho.imhere.core.attendance.model.request.AttendanceRequest;
import gdsc.binaryho.imhere.core.attendance.model.response.AttendanceResponse;
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
import java.time.LocalDateTime;
import java.util.Collections;
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
    private final LectureRepository lectureRepository;
    private final SeoulDateTimeHolder seoulDateTimeHolder;

    @Transactional
    public void takeAttendance(AttendanceRequest attendanceRequest, Long lectureId) {
        Member currentStudent = authenticationHelper.getCurrentMember();
        EnrollmentInfo enrollmentInfo = enrollmentRepository
            .findByMemberIdAndLectureIdAndEnrollmentState(currentStudent.getId(), lectureId, EnrollmentState.APPROVAL)
            .orElseThrow(() -> EnrollmentNotApprovedException.EXCEPTION);

        validateLectureOpen(enrollmentInfo);
        validateAttendanceNumber(enrollmentInfo, attendanceRequest.getAttendanceNumber());

        attend(attendanceRequest, enrollmentInfo);
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

    private void validateAttendanceNumberCorrect(Integer actualAttendanceNumber, int attendanceNumber) {
        if (actualAttendanceNumber != attendanceNumber) {
            throw AttendanceNumberIncorrectException.EXCEPTION;
        }
    }

    @Transactional(readOnly = true)
    public AttendanceResponse getAttendances(Long lectureId) {
        List<Attendance> attendances = attendanceRepository.findAllByLectureId(lectureId);

        if (attendances.isEmpty()) {
            return getNullAttendanceDto(lectureId);
        }

        Lecture lecture = attendances.get(0).getLecture();
        verifyRequestMemberLogInMember(lecture.getMember());

        return new AttendanceResponse(lecture, attendances);
    }

    private void verifyRequestMemberLogInMember(Member lecturer) {
        authenticationHelper.verifyRequestMemberLogInMember(lecturer.getId());
    }

    private AttendanceResponse getNullAttendanceDto(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
            .orElseThrow(() -> LectureNotFoundException.EXCEPTION);
        return new AttendanceResponse(lecture, Collections.emptyList());
    }

    @Transactional(readOnly = true)
    public AttendanceResponse getDayAttendances(Long lectureId, Long milliseconds) {
        LocalDateTime timestamp = getTodaySeoulDateTime(milliseconds);
        List<Attendance> attendances = attendanceRepository
            .findByLectureIdAndTimestampBetween(lectureId, timestamp, timestamp.plusDays(1));

        if (attendances.isEmpty()) {
            return getNullAttendanceDto(lectureId);
        }

        Lecture lecture = attendances.get(0).getLecture();
        verifyRequestMemberLogInMember(lecture.getMember());
        return new AttendanceResponse(lecture, attendances);
    }

    private LocalDateTime getTodaySeoulDateTime(Long milliseconds) {
        return seoulDateTimeHolder.from(milliseconds)
            .withHour(0).withMinute(0).withSecond(0);
    }
}
