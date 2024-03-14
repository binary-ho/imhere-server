package gdsc.binaryho.imhere.core.attendance.application;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StudentAttendedEvent {

        private final long lectureId;
        private final long studentId;
        private final LocalDateTime timestamp;
}
