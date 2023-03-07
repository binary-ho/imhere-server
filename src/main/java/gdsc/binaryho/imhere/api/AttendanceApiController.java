package gdsc.binaryho.imhere.api;

import gdsc.binaryho.imhere.domain.attendance.AttendanceRequest;
import gdsc.binaryho.imhere.service.AttendanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AttendanceApiController {

    private final AttendanceService attendanceService;

    public AttendanceApiController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/api/v1/attendance")
    public ResponseEntity<String> takeAttendance(@RequestBody AttendanceRequest attendanceRequest) {
        try {
            attendanceService.takeAttendance(attendanceRequest);
            return ResponseEntity.ok(HttpStatus.OK.getReasonPhrase());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
