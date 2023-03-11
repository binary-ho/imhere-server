package gdsc.binaryho.imhere.api;

import gdsc.binaryho.imhere.mapper.dtos.AttendanceDto;
import gdsc.binaryho.imhere.mapper.requests.AttendanceRequest;
import gdsc.binaryho.imhere.service.AttendanceService;
import java.rmi.NoSuchObjectException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    @PostMapping("/api/v1/students/attendance/{lecture_id}")
    public ResponseEntity<String> takeAttendance(@RequestBody AttendanceRequest attendanceRequest,
        @PathVariable("lecture_id") Long lectureId) {
        try {
            attendanceService.takeAttendance(attendanceRequest, lectureId);
            return ResponseEntity.ok(HttpStatus.OK.getReasonPhrase());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/api/v1/lecturer/{lecture_id}/attendance")
    public AttendanceDto getAttendance(@PathVariable("lecture_id") Long lectureId)
        throws NoSuchObjectException {
        return attendanceService.getAttendances(lectureId);
    }

    @GetMapping("/api/v1/lecturer/{lecture_id}/attendance/{day_milliseconds}")
    public AttendanceDto getTodayAttendance(@PathVariable("lecture_id") Long lectureId,
        @PathVariable("day_milliseconds") Long milliseconds) throws NoSuchObjectException {
        return attendanceService.getDayAttendances(lectureId, milliseconds);
    }
}
