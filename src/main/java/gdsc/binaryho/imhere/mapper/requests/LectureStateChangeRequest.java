package gdsc.binaryho.imhere.mapper.requests;

import gdsc.binaryho.imhere.domain.lecture.LectureState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LectureStateChangeRequest {

    private LectureState lectureState;
}
