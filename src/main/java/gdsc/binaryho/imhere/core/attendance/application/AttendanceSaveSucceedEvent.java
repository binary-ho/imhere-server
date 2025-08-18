package gdsc.binaryho.imhere.core.attendance.application;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AttendanceSaveSucceedEvent {

    private final long lectureId;
    private final long studentId;
}
