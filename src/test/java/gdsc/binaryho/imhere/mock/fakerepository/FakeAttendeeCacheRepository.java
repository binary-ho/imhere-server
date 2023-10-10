package gdsc.binaryho.imhere.mock.fakerepository;

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
        return data.getOrDefault(
            studentId, Collections.emptySet());
    }

    @Override
    public void cache(Long lectureId, StudentIds studentIds) {
        studentIds
            .getStudentIds()
            .forEach(studentId -> putLectureId(studentId, lectureId));
    }

    private void putLectureId(Long studentId, Long lectureId) {
        data.putIfAbsent(studentId, Collections.emptySet());

        data.get(studentId)
            .add(lectureId);
    }
}
