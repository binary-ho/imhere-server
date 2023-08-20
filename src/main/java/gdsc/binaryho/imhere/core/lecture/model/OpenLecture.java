package gdsc.binaryho.imhere.core.lecture.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OpenLecture {

    private final long id;
    private final String name;
    private final String lecturerName;
    private final int attendanceNumber;
}
