package gdsc.binaryho.imhere.core.attendance.application;


import gdsc.binaryho.imhere.core.attendance.Attendance;
import gdsc.binaryho.imhere.core.attendance.application.port.AttendanceNumberRepository;
import gdsc.binaryho.imhere.core.attendance.exception.AttendanceNumberIncorrectException;
import gdsc.binaryho.imhere.core.attendance.exception.AttendanceTimeExceededException;
import gdsc.binaryho.imhere.core.attendance.infrastructure.AttendanceRepository;
import gdsc.binaryho.imhere.core.attendance.model.request.AttendanceRequest;
import gdsc.binaryho.imhere.core.attendance.model.response.AttendanceResponse;
import gdsc.binaryho.imhere.core.auth.util.AuthenticationHelper;
import gdsc.binaryho.imhere.core.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.core.enrollment.EnrollmentState;
import gdsc.binaryho.imhere.core.enrollment.exception.EnrollmentNotApprovedException;
import gdsc.binaryho.imhere.core.enrollment.infrastructure.EnrollmentInfoRepository;
import gdsc.binaryho.imhere.core.lecture.Lecture;
import gdsc.binaryho.imhere.core.lecture.LectureState;
import gdsc.binaryho.imhere.core.lecture.exception.LectureNotFoundException;
import gdsc.binaryho.imhere.core.lecture.exception.LectureNotOpenException;
import gdsc.binaryho.imhere.core.lecture.infrastructure.LectureRepository;
import gdsc.binaryho.imhere.core.member.Member;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AuthenticationHelper authenticationHelper;
    private final AttendanceRepository attendanceRepository;
    private final EnrollmentInfoRepository enrollmentRepository;
    private final LectureRepository lectureRepository;
    private final AttendanceNumberRepository attendanceNumberRepository;

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
            getLocalDateTime(attendanceRequest.getMilliseconds())
        );

        attendanceRepository.save(attendance);

        Member attendMember = enrollmentInfo.getMember();
        log.info("[출석 완료] {}({}) , 학생 : {} ({})",
            () -> attendance.getLecture().getLectureName(), () -> attendance.getLecture().getId(),
            () -> attendMember.getUnivId(), () -> attendMember.getName());
    }

    private void validateLectureOpen(EnrollmentInfo enrollmentInfo) {
        if (enrollmentInfo.getLecture().getLectureState() != LectureState.OPEN) {
            throw LectureNotOpenException.EXCEPTION;
        }
    }

    private void validateAttendanceNumber(EnrollmentInfo enrollmentInfo, int attendanceNumber) {
        long lectureId = enrollmentInfo.getLecture().getId();
        Integer actualAttendanceNumber = attendanceNumberRepository.getByLectureId(lectureId);

        validateAttendanceNumberNotTimeOut(actualAttendanceNumber);
        validateAttendanceNumberCorrect(actualAttendanceNumber, attendanceNumber);
    }

    private void validateAttendanceNumberNotTimeOut(Integer attendanceNumber) {
        if (Objects.isNull(attendanceNumber)) {
            throw AttendanceTimeExceededException.EXCEPTION;
        }
    }

    private void validateAttendanceNumberCorrect(Integer actualAttendanceNumber, int attendanceNumber) {
        if (actualAttendanceNumber != attendanceNumber) {
            throw AttendanceNumberIncorrectException.EXCEPTION;
        }
    }

    private LocalDateTime getLocalDateTime(Long milliseconds) {
        return LocalDateTime
            .ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.of("Asia/Seoul"));
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
        return new AttendanceResponse(lecture, new ArrayList<>());
    }

    @Transactional(readOnly = true)
    public AttendanceResponse getDayAttendances(Long lectureId, Long milliseconds) {
        LocalDateTime timestamp = getLocalDateTime(milliseconds).withHour(0).withMinute(0).withSecond(0);
        List<Attendance> attendances = attendanceRepository.findByLectureIdAndTimestampBetween(lectureId, timestamp, timestamp.plusDays(1));

        if (attendances.isEmpty()) {
            return getNullAttendanceDto(lectureId);
        }

        Lecture lecture = attendances.get(0).getLecture();
        verifyRequestMemberLogInMember(lecture.getMember());
        return new AttendanceResponse(lecture, attendances);
    }

    @Transactional
    public void saveAttendanceNumber(Long lectureId, int attendanceNumber) {
        attendanceNumberRepository.saveWithLectureIdAsKey(lectureId, attendanceNumber);
    }
}
