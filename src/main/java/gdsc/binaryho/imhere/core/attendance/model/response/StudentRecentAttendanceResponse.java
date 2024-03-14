package gdsc.binaryho.imhere.core.attendance.model.response;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Tag(name = "StudentRecentAttendanceResponse", description = "학생의 최근 출석 시간들")
public class StudentRecentAttendanceResponse {

    private final List<String> timestamps;
}
