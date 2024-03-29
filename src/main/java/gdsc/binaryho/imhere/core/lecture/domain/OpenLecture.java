package gdsc.binaryho.imhere.core.lecture.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OpenLecture {

    private final long id;
    private final String name;
    private final String lecturerName;
    private final int attendanceNumber;

    public static OpenLecture of(Lecture lecture, int attendanceNumber) {
        return new OpenLecture(lecture.getId(), lecture.getLectureName(),
            lecture.getLecturerName(), attendanceNumber);
    }
}
