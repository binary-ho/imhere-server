package gdsc.binaryho.imhere.mock.fakerepository;

import gdsc.binaryho.imhere.core.attendance.application.port.AttendanceHistoryCacheRepository;
import gdsc.binaryho.imhere.core.attendance.domain.AttendanceHistories;
import gdsc.binaryho.imhere.core.attendance.domain.AttendanceHistory;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FakeAttendanceHistoryCacheRepository implements AttendanceHistoryCacheRepository {

    private final Map<String, Set<String>> data = new HashMap<>();

    @Override
    public AttendanceHistories findAllByLectureIdAndStudentId(final long lectureId, final long studentId) {
        List<AttendanceHistory> attendanceHistories = data.getOrDefault(
                AttendanceHistory.convertToKey(lectureId, studentId), Collections.emptySet())
            .stream()
            .map(timestamp -> new AttendanceHistory(lectureId, studentId, timestamp))
            .collect(Collectors.toList());
        return AttendanceHistories.of(attendanceHistories);
    }

    @Override
    public void cache(AttendanceHistory attendanceHistory) {
        data.putIfAbsent(attendanceHistory.getKey(), new HashSet<>());
    }
}
