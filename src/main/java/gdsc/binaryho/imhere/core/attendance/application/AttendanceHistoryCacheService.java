package gdsc.binaryho.imhere.core.attendance.application;

import gdsc.binaryho.imhere.core.attendance.application.port.AttendanceHistoryCacheRepository;
import gdsc.binaryho.imhere.core.attendance.domain.AttendanceHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class AttendanceHistoryCacheService {

    private final AttendanceHistoryCacheRepository attendanceHistoryCacheRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void cache(StudentAttendedEvent event) {
        long lectureId = event.getLectureId();
        long studentId = event.getStudentId();
        String timestamp = event.getTimestamp().toString();

        AttendanceHistory attendanceHistory = AttendanceHistory.of(
            lectureId, studentId, timestamp);
        attendanceHistoryCacheRepository.cache(attendanceHistory);
    }
}
