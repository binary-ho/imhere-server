package gdsc.binaryho.imhere.core.lecture.application;

import gdsc.binaryho.imhere.core.lecture.model.StudentIds;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AttendeeCacheEvent {

    private final Long lectureId;
    private final StudentIds studentIds;
}
