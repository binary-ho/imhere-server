package gdsc.binaryho.imhere.mock.fakerepository;

import gdsc.binaryho.imhere.core.lecture.application.port.OpenLectureCacheRepository;
import gdsc.binaryho.imhere.core.lecture.domain.OpenLecture;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FakeOpenLectureCacheRepository implements OpenLectureCacheRepository {

    private final Map<String, Map<String, String>> data = new HashMap<>();
    private static final String NAME = "name";
    private static final String LECTURER_NAME = "lecturer_name";
    private static final String ATTENDANCE_NUMBER = "attendance_number";

    @Override
    public Optional<OpenLecture> find(Long lectureId) {
        Map<String, String> queryResult = data.get(String.valueOf(lectureId));

        if (queryResult == null || queryResult.isEmpty()) {
            return Optional.empty();
        }

        String name = queryResult.get(NAME);
        String lecturerName = queryResult.get(LECTURER_NAME);
        int attendanceNumber = Integer.parseInt(queryResult.get(ATTENDANCE_NUMBER));

        return Optional.of(new OpenLecture(lectureId, name, lecturerName, attendanceNumber));
    }

    @Override
    public Integer findAttendanceNumber(Long lectureId) {
        return find(lectureId)
            .map(OpenLecture::getAttendanceNumber)
            .orElse(null);
    }

    @Override
    public void cache(OpenLecture openLecture) {
        Map<String, String> hash = Map.of(
            NAME, openLecture.getName(),
            LECTURER_NAME, openLecture.getLecturerName(),
            ATTENDANCE_NUMBER, String.valueOf(openLecture.getAttendanceNumber()
            ));

        data.put(String.valueOf(openLecture.getId()), hash);
    }
}
