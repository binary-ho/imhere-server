package gdsc.binaryho.imhere.core.lecture.application.port;

import gdsc.binaryho.imhere.core.lecture.domain.OpenLecture;
import java.util.Optional;

public interface OpenLectureCacheRepository {

    Optional<OpenLecture> find(Long lectureId);

    Integer findAttendanceNumber(Long lectureId);

    void cache(OpenLecture openLecture);
}
