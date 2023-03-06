package gdsc.binaryho.imhere.domain.lecture;

import lombok.Getter;

@Getter
public class LectureCreateRequest {

    private final Long lecturerId;
    private final String lectureName;

    public LectureCreateRequest(Long lecturerId, String lectureName) {
        this.lecturerId = lecturerId;
        this.lectureName = lectureName;
    }
}
