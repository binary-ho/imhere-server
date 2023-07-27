package gdsc.binaryho.imhere.mock;

import gdsc.binaryho.imhere.core.attendance.application.port.AttendanceNumberRepository;
import java.util.HashMap;
import java.util.Map;

public class FakeAttendanceNumberRepository implements AttendanceNumberRepository {

    private final Map<Long, Integer> data = new HashMap<>();

    @Override
    public Integer getByLectureId(Long lectureId) {
        return data.get(lectureId);
    }

    @Override
    public void saveWithLectureIdAsKey(Long lectureId, int attendanceNumber) {
        data.put(lectureId, attendanceNumber);
    }
}
