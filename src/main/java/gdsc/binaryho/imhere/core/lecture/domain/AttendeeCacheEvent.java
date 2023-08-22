package gdsc.binaryho.imhere.core.lecture.domain;

import gdsc.binaryho.imhere.core.lecture.model.StudentIds;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AttendeeCacheEvent {

    private final Long lectureId;
    private final StudentIds studentIds;
}
