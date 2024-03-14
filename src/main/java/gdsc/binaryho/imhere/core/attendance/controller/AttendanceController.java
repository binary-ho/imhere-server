package gdsc.binaryho.imhere.core.attendance.controller;

import gdsc.binaryho.imhere.core.attendance.application.LecturerAttendanceService;
import gdsc.binaryho.imhere.core.attendance.application.StudentAttendanceService;
import gdsc.binaryho.imhere.core.attendance.model.request.AttendanceRequest;
import gdsc.binaryho.imhere.core.attendance.model.response.LecturerAttendanceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@Tag(name = "Attendance", description = "출석 기능 관련 API입니다.")
@RestController
@RequestMapping("/api/lecture/{lecture_id}/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final StudentAttendanceService studentAttendanceService;
    private final LecturerAttendanceService lecturerAttendanceService;

    @Operation(summary = "학생 출석 시도 API")
    @PostMapping
    public ResponseEntity<Void> takeAttendance(@RequestBody AttendanceRequest attendanceRequest,
        @PathVariable("lecture_id") Long lectureId) {
        studentAttendanceService.takeAttendance(attendanceRequest, lectureId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "특정 강의의 출석 정보 전체를 가져오는 API")
    @GetMapping(params = "!timestamp")
    public ResponseEntity<LecturerAttendanceResponse> getAttendance(
        @PathVariable("lecture_id") Long lectureId) {
        return ResponseEntity.ok(
            lecturerAttendanceService.getLecturerAttendances(lectureId));
    }

    @Operation(summary = "특정 강의의 지정 날짜 출석 리스트를 가져오는 API")
    @GetMapping(params = "timestamp")
    public ResponseEntity<LecturerAttendanceResponse> getTodayAttendance(
        @PathVariable("lecture_id") Long lectureId,
        @Parameter(description = "js Date 객체의 getTime 메서드로 만든 milliseconds 현재 시각")
        @RequestParam Long timestamp) {
        return ResponseEntity.ok(
            lecturerAttendanceService.getLecturerDayAttendances(lectureId, timestamp));
    }
}
