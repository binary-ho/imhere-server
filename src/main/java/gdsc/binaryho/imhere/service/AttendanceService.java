package gdsc.binaryho.imhere.service;


import gdsc.binaryho.imhere.domain.attendance.Attendance;
import gdsc.binaryho.imhere.domain.attendance.AttendanceRepository;
import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfoRepository;
import gdsc.binaryho.imhere.domain.enrollment.EnrollmentState;
import gdsc.binaryho.imhere.domain.lecture.Lecture;
import gdsc.binaryho.imhere.domain.lecture.LectureRepository;
import gdsc.binaryho.imhere.domain.lecture.LectureState;
import gdsc.binaryho.imhere.domain.member.Member;
import gdsc.binaryho.imhere.mapper.dtos.AttendanceDto;
import gdsc.binaryho.imhere.mapper.dtos.AttendanceInfo;
import gdsc.binaryho.imhere.mapper.requests.AttendanceRequest;
import java.rmi.NoSuchObjectException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AuthenticationHelper authenticationHelper;
    private final AttendanceRepository attendanceRepository;
    private final EnrollmentInfoRepository enrollmentRepository;
    private final LectureRepository lectureRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void takeAttendance(AttendanceRequest attendanceRequest, Long lectureId) throws NoSuchObjectException {
        Member currentStudent = authenticationHelper.getCurrentMember();
        EnrollmentInfo enrollmentInfo = enrollmentRepository
            .findByMemberIdAndLectureIdAndEnrollmentState(currentStudent.getId(), lectureId, EnrollmentState.APPROVAL)
            .orElseThrow(IllegalAccessError::new);

        validateLectureOpen(enrollmentInfo);
        validateAttendanceNumber(enrollmentInfo, attendanceRequest.getAttendanceNumber());

        Attendance attendance = Attendance.createAttendance(enrollmentInfo.getMember(),
            enrollmentInfo.getLecture(),
            attendanceRequest.getDistance(), attendanceRequest.getAccuracy(),
            getLocalDateTime(attendanceRequest.getMilliseconds()));

        attendanceRepository.save(attendance);
        log.info("[출석 완료] {}({}) , 학생 : {} ({})",
            () -> attendance.getLecture().getLectureName(), () -> attendance.getLecture().getId(),
            () -> currentStudent.getUnivId(), () -> currentStudent.getName());
    }

    private void validateLectureOpen(EnrollmentInfo enrollmentInfo) throws NoSuchObjectException {
        if (enrollmentInfo.getLecture().getLectureState() != LectureState.OPEN) {
            throw new NoSuchObjectException("lecture is not opened");
        }
    }

    private void validateAttendanceNumber(EnrollmentInfo enrollmentInfo, int attendanceNumber) {
        String actualAttendanceNumber = redisTemplate.opsForValue()
            .get(enrollmentInfo.getLecture().getId().toString());

        validateAttendanceNumberNotTimeOut(actualAttendanceNumber);
        validateAttendanceNumberMatching(actualAttendanceNumber, attendanceNumber);
    }

    private void validateAttendanceNumberNotTimeOut(String attendanceNumber) {
        if (Objects.isNull(attendanceNumber)) {
            throw new IllegalArgumentException("attendance timeout");
        }
    }

    private void validateAttendanceNumberMatching(String actualAttendanceNumber, int attendanceNumber) {
        if (Integer.parseInt(actualAttendanceNumber) != attendanceNumber) {
            throw new IllegalArgumentException("wrong attendanceNumber");
        }
    }

    private LocalDateTime getLocalDateTime(Long milliseconds) {
        return LocalDateTime
            .ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.of("Asia/Seoul"));
    }

    public AttendanceDto getAttendances(Long lectureId) {
        List<Attendance> attendances = attendanceRepository.findAllByLectureId(lectureId);

        if (attendances.isEmpty()) {
            return getNullAttendanceDto(lectureId);
        }

        Lecture lecture = attendances.get(0).getLecture();
        verifyRequestMemberLogInMember(lecture.getMember());

        return getAttendanceDto(lecture, attendances);
    }

    private void verifyRequestMemberLogInMember(Member lecturer) {
        authenticationHelper.verifyRequestMemberLogInMember(lecturer.getId());
    }

    private AttendanceDto getAttendanceDto(Lecture lecture, List<Attendance> attendances) {
        return new AttendanceDto(lecture, AttendanceInfo.getAttendanceInfos(attendances));
    }

    private AttendanceDto getNullAttendanceDto(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow();
        return new AttendanceDto(lecture, new ArrayList<>());
    }

    public AttendanceDto getDayAttendances(Long lectureId, Long milliseconds) {
        LocalDateTime timestamp = getLocalDateTime(milliseconds).withHour(0).withMinute(0).withSecond(0);
        List<Attendance> attendances = attendanceRepository.findByLectureIdAndTimestampBetween(lectureId, timestamp, timestamp.plusDays(1));

        if (attendances.isEmpty()) {
            return getNullAttendanceDto(lectureId);
        }

        Lecture lecture = attendances.get(0).getLecture();
        verifyRequestMemberLogInMember(lecture.getMember());
        return getAttendanceDto(lecture, attendances);
    }
}
