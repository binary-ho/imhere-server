package gdsc.binaryho.imhere.core.lecture.model;

import gdsc.binaryho.imhere.core.lecture.Lecture;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OpenLecture {

    private final long id;
    private final String name;
    private final String lecturerName;
    private final int attendanceNumber;

    public static OpenLecture from(Lecture lecture, int attendanceNumber) {
        return OpenLecture.builder()
            .id(lecture.getId())
            .name(lecture.getLectureName())
            .lecturerName(lecture.getLecturerName())
            .attendanceNumber(attendanceNumber)
            .build();
    }

    public static OpenLecture from(Map<Object, Object> openLecture) {
        long id = (long) openLecture.get(OpenLectureFieldKeys.ID);
        String name = (String) openLecture.get(OpenLectureFieldKeys.NAME);
        String lecturerName = (String) openLecture.get(OpenLectureFieldKeys.LECTURER_NAME);
        int attendanceNumber = (int) openLecture.get(OpenLectureFieldKeys.ATTENDANCE_NUMBER);

        return OpenLecture.builder()
            .id(id)
            .name(name)
            .lecturerName(lecturerName)
            .attendanceNumber(attendanceNumber)
            .build();
    }
}
