package gdsc.binaryho.imhere.core.lecture.application;

import gdsc.binaryho.imhere.core.lecture.application.port.AttendeeCacheRepository;
import gdsc.binaryho.imhere.core.lecture.application.port.OpenLectureCacheRepository;
import gdsc.binaryho.imhere.core.lecture.domain.AttendeeCacheEvent;
import gdsc.binaryho.imhere.core.lecture.domain.OpenLecture;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class OpenLectureService {

    private final OpenLectureCacheRepository openLectureCacheRepository;
    private final AttendeeCacheRepository attendeeCacheRepository;

    @Transactional(readOnly = true)
    public Optional<OpenLecture> find(Long lectureId) {
        return openLectureCacheRepository.find(lectureId);
    }

    @Transactional(readOnly = true)
    public Integer findAttendanceNumber(Long lectureId) {
        return openLectureCacheRepository.findAttendanceNumber(lectureId);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void cache(AttendeeCacheEvent event) {
        attendeeCacheRepository.cache(event.getLectureId(), event.getStudentIds());
    }
}
