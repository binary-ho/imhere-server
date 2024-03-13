package gdsc.binaryho.imhere.core.attendance.infrastructure;

import gdsc.binaryho.imhere.core.attendance.Attendance;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findAllByLectureId(Long lectureId);

    List<Attendance> findByLectureIdAndTimestampBetween(Long lectureId, LocalDateTime startOfDay,
        LocalDateTime endOfDay);

    List<Attendance> findByLectureIdAndStudentIdAndTimestampBetween(
        Long lectureId, Long studentId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
