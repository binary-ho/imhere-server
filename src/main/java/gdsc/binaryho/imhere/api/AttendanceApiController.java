package gdsc.binaryho.imhere.api;

import gdsc.binaryho.imhere.mapper.requests.AttendanceRequest;
import gdsc.binaryho.imhere.service.AttendanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AttendanceApiController {

    private final AttendanceService attendanceService;

    public AttendanceApiController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    /*
    *  학생들의 출석 시도 메서드
    * */
    @PostMapping("/api/v1/students/{student_id}/attendance/{lecture_id}")
    public ResponseEntity<String> takeAttendance(@RequestBody AttendanceRequest attendanceRequest,
        @PathVariable("student_id") Long student_id, @PathVariable("lecture_id") Long lecture_id) {
        try {
            attendanceService.takeAttendance(attendanceRequest, student_id, lecture_id);
            return ResponseEntity.ok(HttpStatus.OK.getReasonPhrase());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
