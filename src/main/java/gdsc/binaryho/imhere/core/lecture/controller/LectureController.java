package gdsc.binaryho.imhere.core.lecture.controller;

import gdsc.binaryho.imhere.core.attendance.application.AttendanceNumberDto;
import gdsc.binaryho.imhere.core.lecture.Lecture;
import gdsc.binaryho.imhere.core.lecture.LectureRepository;
import gdsc.binaryho.imhere.core.lecture.LectureState;
import gdsc.binaryho.imhere.core.lecture.application.LectureDto;
import gdsc.binaryho.imhere.core.lecture.application.LectureService;
import gdsc.binaryho.imhere.core.lecture.application.request.LectureCreateRequest;
import gdsc.binaryho.imhere.exception.ImhereException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/lectures")
@RequiredArgsConstructor
public class LectureController {

    private final LectureService lectureService;
    private final LectureRepository lectureRepository;

    private final static String STATUS = "status=";

    @Operation(summary = "학생이 수강신청을 위해 개설된 모든 강의 리스트를 가져오는 API")
    @GetMapping
    public List<LectureDto> getAllLectures() {
        List<Lecture> lectures = lectureRepository.findAllByLectureStateNot(LectureState.TERMINATED);
        return lectures.stream()
            .map(LectureDto::createLectureDto)
            .collect(Collectors.toList());
    }

    @Operation(summary = "로그인한 학생이 수강중인 강의 리스트를 가져오는 API")
    @GetMapping(params = STATUS + "enrolled")
    public List<LectureDto> getStudentLectures() {
        List<Lecture> lectures = lectureService.getStudentLectures();
        return lectures.stream()
            .map(LectureDto::createLectureDto)
            .collect(Collectors.toList());
    }

    @Operation(summary = "로그인한 학생이 출석 가능한 OPEN 상태 강의를 가져오는 API")
    @GetMapping(params = STATUS + "opened")
    public List<LectureDto> getStudentOpenLectures() {
        List<Lecture> lectures = lectureService.getStudentOpenLectures();
        return lectures.stream()
            .map(LectureDto::createLectureDto)
            .collect(Collectors.toList());
    }

    @Operation(summary = "로그인한 강사가 만든 강의를 가져오는 API")
    @GetMapping(params = STATUS + "owned")
    public List<LectureDto> getOwnedLectures() {
        return lectureService.getOwnedLectures();
    }

    @Operation(summary = "로그인한 강사가 강의를 생성하는 API")
    @PostMapping
    public ResponseEntity<String> createLecture(@RequestBody LectureCreateRequest request) {
        try {
            lectureService.createLecture(request);
            return ResponseEntity.ok(HttpStatus.OK.toString());
        } catch (ImhereException error) {
            return ResponseEntity.status(error.getErrorCode().getCode()).build();
        }
    }

    @Operation(summary = "로그인한 강사가 강의를 OPEN하고 출석 번호를 발급 받는 API")
    @PostMapping("/{lecture_id}/open")
    public ResponseEntity<AttendanceNumberDto> openLectureAndGetAttendanceNumber(@PathVariable("lecture_id") Long lectureId) {
        try {
            int attendanceNumber = lectureService.openLectureAndGetAttendanceNumber(lectureId);
            return ResponseEntity.ok(new AttendanceNumberDto(attendanceNumber));
        } catch (ImhereException e) {
            log.error("[강의 OPEN ERROR] : " + e);
            return ResponseEntity.status(e.getErrorCode().getCode()).build();
        }
    }

    @Operation(summary = "로그인한 강사가 강의를 CLOSED 상태로 바꾸는 API")
    @PostMapping("/{lecture_id}/close")
    public ResponseEntity<String> changeLectureState(@PathVariable("lecture_id") Long lecture_id) {
        try {
            lectureService.closeLecture(lecture_id);
            return ResponseEntity.ok(HttpStatus.OK.toString());
        } catch (ImhereException e) {
            return ResponseEntity.status(e.getErrorCode().getCode()).build();
        }
    }
}
