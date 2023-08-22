package gdsc.binaryho.imhere.core.lecture.application;

import gdsc.binaryho.imhere.core.lecture.application.port.OpenLectureCacheRepository;
import gdsc.binaryho.imhere.core.lecture.domain.OpenLecture;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenLectureService {

    private final OpenLectureCacheRepository openLectureCacheRepository;

    public Optional<OpenLecture> find(Long lectureId) {
        return openLectureCacheRepository.findByLectureId(lectureId);
    }

    public Integer findAttendanceNumber(Long lectureId) {
        return openLectureCacheRepository.findAttendanceNumber(lectureId);
    }
}
