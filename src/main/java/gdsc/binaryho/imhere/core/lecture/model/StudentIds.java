package gdsc.binaryho.imhere.core.lecture.model;

import java.util.Collections;
import java.util.List;
import lombok.Getter;

@Getter
public class StudentIds {

    private final List<Long> studentIds;

    public StudentIds(List<Long> studentIds) {
        this.studentIds = studentIds;
    }

    public StudentIds(Long studentId) {
        this.studentIds = Collections.singletonList(studentId);
    }
}
