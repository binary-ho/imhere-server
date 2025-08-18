package gdsc.binaryho.imhere.core.attendance.application;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AttendanceRequestedEvent {

    private final long lectureId;
    private final long studentId;
}
