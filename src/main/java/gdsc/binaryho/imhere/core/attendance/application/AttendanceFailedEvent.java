package gdsc.binaryho.imhere.core.attendance.application;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AttendanceFailedEvent {

    private final long lectureId;
    private final long studentId;
    private final Throwable exception;
}
