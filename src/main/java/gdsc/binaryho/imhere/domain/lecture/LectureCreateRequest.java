package gdsc.binaryho.imhere.domain.lecture;

import lombok.Getter;

@Getter
public class LectureCreateRequest {

    private final String lectureName;

    public LectureCreateRequest(String lectureName) {
        this.lectureName = lectureName;
    }
}
