package gdsc.binaryho.imhere.core.lecture.application.port;

import gdsc.binaryho.imhere.core.lecture.model.StudentIds;
import java.util.Set;

public interface AttendeeCacheRepository {

    Boolean isStudentOpenLectureExist(Long studentId, Long lectureId);

    Set<Long> findAllAttendLectureId(Long studentId);

    void cache(Long lectureId, StudentIds studentIds);
}
