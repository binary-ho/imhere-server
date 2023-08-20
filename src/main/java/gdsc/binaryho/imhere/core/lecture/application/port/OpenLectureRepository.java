package gdsc.binaryho.imhere.core.lecture.application.port;

import gdsc.binaryho.imhere.core.lecture.model.OpenLecture;
import java.util.Optional;

public interface OpenLectureRepository {

    Optional<OpenLecture> findByLectureId(Long lectureId);

    Integer findAttendanceNumber(Long lectureId);

    void save(OpenLecture openLecture);
}
