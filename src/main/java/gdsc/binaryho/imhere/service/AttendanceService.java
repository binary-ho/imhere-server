package gdsc.binaryho.imhere.service;


import gdsc.binaryho.imhere.domain.attendance.Attendance;
import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfo;
import gdsc.binaryho.imhere.domain.enrollment.EnrollmentInfoRepository;
import gdsc.binaryho.imhere.domain.lecture.LectureState;
import gdsc.binaryho.imhere.domain.member.Member;
import gdsc.binaryho.imhere.mapper.requests.AttendanceRequest;
import java.rmi.NoSuchObjectException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final EnrollmentInfoRepository enrollmentRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void takeAttendance(AttendanceRequest attendanceRequest, Long lectureId) throws NoSuchObjectException {
        Member currentStudent = AuthenticationService.getCurrentMember();
        EnrollmentInfo enrollmentInfo = enrollmentRepository
            .findByMemberIdAndLectureId(currentStudent.getId(), lectureId)
            .orElseThrow(IllegalAccessError::new);

        validateLectureOpen(enrollmentInfo);
        validateAttendanceNumber(enrollmentInfo, attendanceRequest.getAttendanceNumber());

        LocalDateTime localDateTime = getLocalDateTime(attendanceRequest.getMilliseconds());

        Attendance.createAttendance(enrollmentInfo.getMember(), enrollmentInfo.getLecture(),
            attendanceRequest.getDistance(), attendanceRequest.getAccuracy(), localDateTime);
    }

    private void validateLectureOpen(EnrollmentInfo enrollmentInfo) throws NoSuchObjectException {
        if (enrollmentInfo.getLecture().getLectureState() != LectureState.OPEN) {
            throw new NoSuchObjectException("lecture is not opened");
        }
    }

    private void validateAttendanceNumber(EnrollmentInfo enrollmentInfo, int attendanceNumber) {
        String actualAttendanceNumber = Objects.requireNonNull(
            redisTemplate.opsForValue()
                .get(enrollmentInfo.getLecture().getId().toString())
        );

        if (Integer.parseInt(actualAttendanceNumber) != attendanceNumber) {
            throw new IllegalArgumentException("wrong attendanceNumber");
        }
    }

    private LocalDateTime getLocalDateTime(Long milliseconds) {
        return LocalDateTime
            .ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.of("Asia/Seoul"));
    }
}
