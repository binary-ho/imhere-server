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
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
    }

    private void validateLectureOpen(EnrollmentInfo enrollmentInfo) throws NoSuchObjectException {
        if (enrollmentInfo.getLecture().getLectureState() != LectureState.OPEN) {
            throw new NoSuchObjectException("lecture is not opened");
        }
    }

    private void validateAttendanceNumber(EnrollmentInfo enrollmentInfo, int attendanceNumber) {
        String actualAttendanceNumber = redisTemplate.opsForValue()
            .get(enrollmentInfo.getLecture().getId().toString());

        if (actualAttendanceNumber == null) {
            throw new IllegalArgumentException("attendance timeout");
        }

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
