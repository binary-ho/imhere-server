package gdsc.binaryho.imhere.core.attendance.application;


import gdsc.binaryho.imhere.core.attendance.Attendance;
import gdsc.binaryho.imhere.core.attendance.infrastructure.AttendanceRepository;
import gdsc.binaryho.imhere.core.attendance.model.response.LecturerAttendanceResponse;
import gdsc.binaryho.imhere.core.member.Role;
import gdsc.binaryho.imhere.security.util.AuthenticationHelper;
import gdsc.binaryho.imhere.util.SeoulDateTimeHolder;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class LecturerAttendanceService {

    private final AttendanceRepository attendanceRepository;

    private final SeoulDateTimeHolder seoulDateTimeHolder;
    private final AuthenticationHelper authenticationHelper;

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

    private LocalDateTime getTodaySeoulDateTime(Long milliseconds) {
        return seoulDateTimeHolder.from(milliseconds)
            .withHour(0).withMinute(0).withSecond(0);
    }
}
