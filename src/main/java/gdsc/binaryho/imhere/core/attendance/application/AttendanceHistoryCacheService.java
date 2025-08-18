package gdsc.binaryho.imhere.core.attendance.application;

import static gdsc.binaryho.imhere.core.attendance.domain.AttendanceHistory.createAcceptedAttendanceHistory;
import static gdsc.binaryho.imhere.core.attendance.domain.AttendanceHistory.createAwaitAttendanceHistory;
import static gdsc.binaryho.imhere.core.attendance.domain.AttendanceHistory.createFailedAttendanceHistory;

import gdsc.binaryho.imhere.core.attendance.application.port.AttendanceHistoryCacheRepository;
import gdsc.binaryho.imhere.core.attendance.domain.AttendanceHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Log4j2
@Service
@RequiredArgsConstructor
public class AttendanceHistoryCacheService {

    private final AttendanceHistoryCacheRepository attendanceHistoryCacheRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void cacheAttendanceHistory(AttendanceRequestedEvent event) {
        long lectureId = event.getLectureId();
        long studentId = event.getStudentId();

        AttendanceHistory attendanceHistory = createAwaitAttendanceHistory(lectureId, studentId);
        attendanceHistoryCacheRepository.cache(attendanceHistory);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void saveAttendance(AttendanceSaveSucceedEvent event) {
        long lectureId = event.getLectureId();
        long studentId = event.getStudentId();

        AttendanceHistory attendanceHistory = createAcceptedAttendanceHistory(lectureId, studentId);
        attendanceHistoryCacheRepository.cache(attendanceHistory);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void saveAttendance(AttendanceFailedEvent event) {
        long lectureId = event.getLectureId();
        long studentId = event.getStudentId();

        AttendanceHistory attendanceHistory = createFailedAttendanceHistory(lectureId, studentId);
        attendanceHistoryCacheRepository.cache(attendanceHistory);
        logAttendanceFailed(studentId, lectureId, event.getException());
    }

    private void logAttendanceFailed(Long studentId, Long lectureId, RuntimeException exception) {
        String exceptionName = exception.getClass().getName();
        String exceptionMessage = exception.getMessage();
        log.info("[출석 실패] 학생 id {}, 수업 id : {}, 예외 이름 : {}, 예외 메시지 : {}",
            studentId, lectureId, exceptionName, exceptionMessage);
    }
}
