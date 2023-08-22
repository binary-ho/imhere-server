package gdsc.binaryho.imhere.core.lecture.application.port;

import gdsc.binaryho.imhere.core.lecture.domain.OpenLecture;
import java.util.Optional;

public interface OpenLectureCacheRepository {

    Optional<OpenLecture> findByLectureId(Long lectureId);

    Integer findAttendanceNumber(Long lectureId);

    void save(OpenLecture openLecture);
}
