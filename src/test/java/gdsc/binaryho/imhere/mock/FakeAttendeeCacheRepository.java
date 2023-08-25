package gdsc.binaryho.imhere.mock;

import gdsc.binaryho.imhere.core.lecture.application.port.AttendeeCacheRepository;
import gdsc.binaryho.imhere.core.lecture.model.StudentIds;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FakeAttendeeCacheRepository implements AttendeeCacheRepository {

    private final Map<Long, Set<Long>> data = new HashMap<>();

    @Override
    public Set<Long> findAllAttendLectureId(Long studentId) {
        Set<Long> ids = data.get(studentId);

        if (ids == null) {
            return Collections.emptySet();
        }

        return ids;
    }

    @Override
    public void cache(Long lectureId, StudentIds studentIds) {
        studentIds
            .getStudentIds()
            .forEach(studentId -> putLectureId(studentId, lectureId));
    }

    private void putLectureId(Long studentId, Long lectureId) {
        Set<Long> result = data.get(studentId);
        if (result == null || result.isEmpty()) {
            data.put(studentId, Collections.singleton(lectureId));
        } else {
            result.add(lectureId);
        }
    }
}
