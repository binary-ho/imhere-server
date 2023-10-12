package gdsc.binaryho.imhere.core.lecture.controller;

import gdsc.binaryho.imhere.core.lecture.application.LectureService;
import gdsc.binaryho.imhere.core.lecture.model.request.LectureCreateRequest;
import gdsc.binaryho.imhere.core.lecture.model.response.AttendanceNumberResponse;
import gdsc.binaryho.imhere.core.lecture.model.response.LectureResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@Tag(name = "Lecture", description = "강의 관련 API입니다.")
@RestController
@RequestMapping("/api/lecture")
@RequiredArgsConstructor
public class LectureController {

    private final LectureService lectureService;

    private static final String STATUS = "status=";

    @Operation(summary = "학생이 수강신청을 위해 개설된 모든 강의 리스트를 가져오는 API")
    @GetMapping
    public ResponseEntity<LectureResponse> getAllLectures() {
        return ResponseEntity.ok(
            lectureService.getAllLecturesForEnrollment());
    }

    @Operation(summary = "로그인한 학생이 수강중인 강의 리스트를 가져오는 API")
    @GetMapping(params = STATUS + "enrolled")
    public ResponseEntity<LectureResponse> getStudentLectures() {
        return ResponseEntity.ok(
            lectureService.getStudentLectures());
    }

    @Operation(summary = "로그인한 학생이 출석 가능한 OPEN 상태 강의를 가져오는 API")
    @GetMapping(params = STATUS + "opened")
    public ResponseEntity<LectureResponse> getStudentOpenLectures() {
        return ResponseEntity
            .ok(lectureService.getStudentOpenLectures());
    }

    @Operation(summary = "로그인한 강사가 만든 강의를 가져오는 API")
    @GetMapping(params = STATUS + "owned")
    public ResponseEntity<LectureResponse> getOwnedLectures() {
        return ResponseEntity.ok(lectureService.getOwnedLectures());
    }

    @Operation(summary = "로그인한 강사가 강의를 생성하는 API")
    @PostMapping
    public ResponseEntity<Void> createLecture(@RequestBody LectureCreateRequest request) {
        lectureService.createLecture(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그인한 강사가 강의를 OPEN하고 출석 번호를 발급 받는 API")
    @PostMapping("/{lecture_id}/open")
    public ResponseEntity<AttendanceNumberResponse> openLectureAndGetAttendanceNumber(@PathVariable("lecture_id") Long lectureId) {
        return ResponseEntity
            .ok(lectureService.openLectureAndGenerateAttendanceNumber(lectureId));
    }

    @Operation(summary = "로그인한 강사가 강의를 CLOSED 상태로 바꾸는 API")
    @PostMapping("/{lecture_id}/close")
    public ResponseEntity<Void> changeLectureState(@PathVariable("lecture_id") Long lecture_id) {
        lectureService.closeLecture(lecture_id);
        return ResponseEntity.ok().build();
    }
}
