package gdsc.binaryho.imhere.api;

import gdsc.binaryho.imhere.mapper.dtos.AttendanceDto;
import gdsc.binaryho.imhere.mapper.requests.AttendanceRequest;
import gdsc.binaryho.imhere.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Attendance", description = "출석 기능 관련 API입니다.")
@RestController
@RequestMapping("/api/v1/attendance")
public class AttendanceApiController {

    private final AttendanceService attendanceService;

    public AttendanceApiController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @Operation(summary = "학생 출석 시도 API")
    @PostMapping("/{lecture_id}")
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

    @Operation(summary = "특정 강의의 출석 정보 전체를 가져오는 API")
    @GetMapping("/{lecture_id}")
    public AttendanceDto getAttendance(@PathVariable("lecture_id") Long lectureId) {
        return attendanceService.getAttendances(lectureId);
    }

    @Operation(summary = "특정 강의의 지정 날짜 출석 리스트를 가져오는 API")
    @GetMapping("/{lecture_id}/{day_milliseconds}")
    public AttendanceDto getTodayAttendance(@PathVariable("lecture_id") Long lectureId,
        @Parameter(description = "js Date 객체의 getTime 메서드로 만든 milliseconds 현재 시각") @PathVariable("day_milliseconds") Long milliseconds) {
        return attendanceService.getDayAttendances(lectureId, milliseconds);
    }
}
