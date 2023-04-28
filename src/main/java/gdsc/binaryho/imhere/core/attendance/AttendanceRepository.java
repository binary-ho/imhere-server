package gdsc.binaryho.imhere.core.attendance;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findAllByLectureId(Long lectureId);
    List<Attendance> findByLectureIdAndTimestampBetween(Long lectureId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}